package net.hbase.secondaryindex.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

public class JsonUtil {
	private final Pattern patternKey = Pattern
			.compile("^([a-zA-Z0-9_\\-\\:\\s]+).*");
	private final Pattern patternIndex = Pattern.compile("\\[([0-9]+|\\*)\\]");

	private static final JsonFactory JSON_FACTORY = new JsonFactory();
	static {
		// Allows for unescaped ASCII control characters in JSON values
		JSON_FACTORY.enable(Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
		JSON_FACTORY.enable(Feature.ALLOW_SINGLE_QUOTES);
	}
	private static final ObjectMapper MAPPER = new ObjectMapper(JSON_FACTORY);
	@SuppressWarnings("deprecation")
	private static final JavaType MAP_TYPE = TypeFactory.fromClass(Map.class);

	// An LRU cache using a linked hash map
	static class HashCache<K, V> extends LinkedHashMap<K, V> {

		private static final int CACHE_SIZE = 16;
		private static final int INIT_SIZE = 32;
		private static final float LOAD_FACTOR = 0.6f;

		HashCache() {
			super(INIT_SIZE, LOAD_FACTOR);
		}

		private static final long serialVersionUID = 1;

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() > CACHE_SIZE;
		}

	}

	static Map<String, Object> extractObjectCache = new HashCache<String, Object>();
	static Map<String, String[]> pathExprCache = new HashCache<String, String[]>();
	static Map<String, ArrayList<String>> indexListCache = new HashCache<String, ArrayList<String>>();
	static Map<String, String> mKeyGroup1Cache = new HashCache<String, String>();
	static Map<String, Boolean> mKeyMatchesCache = new HashCache<String, Boolean>();

	Text result = new Text();

	public JsonUtil() {
	}

	@SuppressWarnings("rawtypes")
	public List<String> evaluateArray(String jsonString, String pathString)
			throws JsonParseException, JsonMappingException, IOException {
		List<String> list = new ArrayList<String>();
		if (jsonString == null || jsonString == "" || pathString == null
				|| pathString == "") {
			return null;
		}

		List result = MAPPER.readValue(jsonString, new TypeReference<List>() {
		});
		String[] arr = pathString.split(",", -1);
		for (Object o : result) {
			if (o instanceof LinkedHashMap) {
				LinkedHashMap m = (LinkedHashMap) o;
				if (arr.length == 1)
					list.add(m.get(pathString).toString());
				else if (arr.length > 1) {
					StringBuilder sb = new StringBuilder();
					for (String path : arr) {
						sb.append(m.get(path).toString() + ",");
					}
					if (sb.length() > 0)
						sb.setLength(sb.length() - 1);
					list.add(sb.toString());
				}
			} else if (o instanceof String) {
				String str = (String) o;
				str = str.replaceAll(Const.ROWKEY_DEFAULT_SEPARATOR, ",");
				list.add(str);
			}
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	public Set<String> evaluateDistinctArray(String jsonString,
			String pathString) throws JsonParseException, JsonMappingException,
			IOException {
		Set<String> set = new HashSet<String>();
		if (jsonString == null || jsonString == "" || pathString == null
				|| pathString == "") {
			return null;
		}

		List result = MAPPER.readValue(jsonString, new TypeReference<List>() {
		});
		for (Object o : result) {
			if (o instanceof LinkedHashMap) {
				LinkedHashMap m = (LinkedHashMap) o;
				set.add(m.get(pathString).toString());
			} else if (o instanceof String) {
				String str = (String) o;
				String[] arr = str.split(Const.ROWKEY_DEFAULT_SEPARATOR, -1);
				for (String s : arr) {
					set.add(s);
				}
			}
		}
		return set;
	}

	/**
	 * Extract json object from a json string based on json path specified, and
	 * return json string of the extracted json object. It will return null if
	 * the input json string is invalid.
	 * 
	 * A limited version of JSONPath supported: $ : Root object . : Child
	 * operator [] : Subscript operator for array * : Wildcard for []
	 * 
	 * Syntax not supported that's worth noticing: '' : Zero length string as
	 * key .. : Recursive descent &amp;#064; : Current object/element () :
	 * Script expression ?() : Filter (script) expression. [,] : Union operator
	 * [start:end:step] : array slice operator
	 * 
	 * @param jsonString
	 *            the json string.
	 * @param pathString
	 *            the json path expression.
	 * @return json string or null when an error happens.
	 */
	public Text evaluate(String jsonString, String pathString) {

		if (jsonString == null || jsonString == "" || pathString == null
				|| pathString == "") {
			return null;
		}

		// Cache pathExpr
		String[] pathExpr = pathExprCache.get(pathString);
		if (pathExpr == null) {
			pathExpr = pathString.split("\\.", -1);
			pathExprCache.put(pathString, pathExpr);
		}

		if (!pathExpr[0].equalsIgnoreCase("$")) {
			return null;
		}
		// Cache extractObject
		Object extractObject = extractObjectCache.get(jsonString);
		if (extractObject == null) {
			try {
				extractObject = MAPPER.readValue(jsonString, MAP_TYPE);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			extractObjectCache.put(jsonString, extractObject);
		}
		for (int i = 1; i < pathExpr.length; i++) {
			if (extractObject == null) {
				return null;
			}
			extractObject = extract(extractObject, pathExpr[i]);
		}
		if (extractObject instanceof Map || extractObject instanceof List) {
			try {
				result.set(MAPPER.writeValueAsString(extractObject));
			} catch (Exception e) {
				return null;
			}
		} else if (extractObject != null) {
			result.set(extractObject.toString());
		} else {
			return null;
		}
		return result;
	}

	private Object extract(Object json, String path) {

		// Cache patternkey.matcher(path).matches()
		Matcher mKey = null;
		Boolean mKeyMatches = mKeyMatchesCache.get(path);
		if (mKeyMatches == null) {
			mKey = patternKey.matcher(path);
			mKeyMatches = mKey.matches() ? Boolean.TRUE : Boolean.FALSE;
			mKeyMatchesCache.put(path, mKeyMatches);
		}
		if (!mKeyMatches.booleanValue()) {
			return null;
		}

		// Cache mkey.group(1)
		String mKeyGroup1 = mKeyGroup1Cache.get(path);
		if (mKeyGroup1 == null) {
			if (mKey == null) {
				mKey = patternKey.matcher(path);
			}
			mKeyGroup1 = mKey.group(1);
			mKeyGroup1Cache.put(path, mKeyGroup1);
		}
		json = extract_json_withkey(json, mKeyGroup1);

		// Cache indexList
		ArrayList<String> indexList = indexListCache.get(path);
		if (indexList == null) {
			Matcher mIndex = patternIndex.matcher(path);
			indexList = new ArrayList<String>();
			while (mIndex.find()) {
				indexList.add(mIndex.group(1));
			}
			indexListCache.put(path, indexList);
		}

		if (indexList.size() > 0) {
			json = extract_json_withindex(json, indexList);
		}

		return json;
	}

	List<Object> jsonList = new ArrayList<Object>();

	@SuppressWarnings("unchecked")
	private Object extract_json_withindex(Object json,
			ArrayList<String> indexList) {

		jsonList.clear();
		jsonList.add(json);
		Iterator<String> itr = indexList.iterator();
		while (itr.hasNext()) {
			String index = itr.next();
			List<Object> tmp_jsonList = new ArrayList<Object>();
			if (index.equalsIgnoreCase("*")) {
				for (int i = 0; i < jsonList.size(); i++) {
					Object array = jsonList.get(i);
					if (array instanceof List) {
						for (int j = 0; j < ((List<Object>) array).size(); j++) {
							tmp_jsonList.add(((List<Object>) array).get(j));
						}
					}
				}
				jsonList = tmp_jsonList;
			} else {
				for (int i = 0; i < (jsonList).size(); i++) {
					Object array = jsonList.get(i);
					int indexValue = Integer.parseInt(index);
					if (!(array instanceof List)) {
						continue;
					}
					if (indexValue >= ((List<Object>) array).size()) {
						return null;
					}
					tmp_jsonList.add(((List<Object>) array).get(indexValue));
					jsonList = tmp_jsonList;
				}
			}
		}
		if (jsonList.isEmpty()) {
			return null;
		}
		return (jsonList.size() > 1) ? new ArrayList<Object>(jsonList)
				: jsonList.get(0);
	}

	@SuppressWarnings("unchecked")
	private Object extract_json_withkey(Object json, String path) {
		if (json instanceof List) {
			List<Object> jsonArray = new ArrayList<Object>();
			for (int i = 0; i < ((List<Object>) json).size(); i++) {
				Object json_elem = ((List<Object>) json).get(i);
				Object json_obj = null;
				if (json_elem instanceof Map) {
					json_obj = ((Map<String, Object>) json_elem).get(path);
				} else {
					continue;
				}
				if (json_obj instanceof List) {
					for (int j = 0; j < ((List<Object>) json_obj).size(); j++) {
						jsonArray.add(((List<Object>) json_obj).get(j));
					}
				} else if (json_obj != null) {
					jsonArray.add(json_obj);
				}
			}
			return (jsonArray.size() == 0) ? null : jsonArray;
		} else if (json instanceof Map) {
			return ((Map<String, Object>) json).get(path);
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws JsonParseException,
			IOException {
		String json = "{'channel':'14','videoid':'5815053'}";

		json = "[{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"剧情\",\"counter\":2,\"time\":2,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"动作\",\"counter\":36,\"time\":36,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"剧情\",\"counter\":10,\"time\":10,\"type\":\"电视\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"港台\",\"category\":\"剧情\",\"counter\":22,\"time\":22,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"战争\",\"counter\":8,\"time\":8,\"type\":\"电视\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"惊悚\",\"counter\":35,\"time\":35,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"剧情\",\"counter\":5,\"time\":5,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"爱情\",\"counter\":18,\"time\":18,\"type\":\"电视\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"港台\",\"category\":\"动作\",\"counter\":124,\"time\":124,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"港台\",\"category\":\"喜剧\",\"counter\":21,\"time\":21,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"科幻\",\"counter\":34,\"time\":34,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"喜剧\",\"counter\":1,\"time\":1,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"古装\",\"counter\":7,\"time\":7,\"type\":\"电视\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"都市\",\"counter\":47,\"time\":47,\"type\":\"电视\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"爱情\",\"counter\":3,\"time\":3,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"爱情\",\"counter\":13,\"time\":13,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"喜剧\",\"counter\":16,\"time\":16,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"港台\",\"category\":\"爱情\",\"counter\":23,\"time\":23,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"犯罪\",\"counter\":135,\"time\":135,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"喜剧\",\"counter\":281,\"time\":281,\"type\":\"电视\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"韩国\",\"category\":\"剧情\",\"counter\":358,\"time\":358,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"动作\",\"counter\":4,\"time\":4,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"港台\",\"category\":\"犯罪\",\"counter\":33,\"time\":33,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"日本\",\"category\":\"校园\",\"counter\":43,\"time\":43,\"type\":\"动漫\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"警匪\",\"counter\":9,\"time\":9,\"type\":\"电视\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"日本\",\"category\":\"热血\",\"counter\":42,\"time\":42,\"type\":\"动漫\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"港台\",\"category\":\"悬疑\",\"counter\":55,\"time\":55,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"家庭\",\"counter\":58,\"time\":58,\"type\":\"电视\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"港台\",\"category\":\"都市\",\"counter\":70,\"time\":70,\"type\":\"电视\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"战争\",\"counter\":119,\"time\":119,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"娱乐\",\"counter\":284,\"time\":284,\"type\":\"综艺\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"港台\",\"category\":\"警匪\",\"counter\":363,\"time\":363,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"韩国\",\"category\":\"爱情\",\"counter\":449,\"time\":449,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"港台\",\"category\":\"惊悚\",\"counter\":32,\"time\":32,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"悬疑\",\"counter\":61,\"time\":61,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"悬疑\",\"counter\":226,\"time\":226,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"悬疑\",\"counter\":11,\"time\":11,\"type\":\"电视\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"伦理\",\"counter\":29,\"time\":29,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"日本\",\"category\":\"励志\",\"counter\":44,\"time\":44,\"type\":\"动漫\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"韩国\",\"category\":\"浪漫\",\"counter\":69,\"time\":69,\"type\":\"电视\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"校园\",\"counter\":71,\"time\":71,\"type\":\"动漫\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"日本\",\"category\":\"冒险\",\"counter\":77,\"time\":77,\"type\":\"动漫\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"犯罪\",\"counter\":161,\"time\":161,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"伦理\",\"counter\":162,\"time\":162,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"魔幻\",\"counter\":245,\"time\":245,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"武侠\",\"counter\":20,\"time\":20,\"type\":\"电视\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"日本\",\"category\":\"恶搞\",\"counter\":45,\"time\":45,\"type\":\"动漫\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"日本\",\"category\":\"魔幻\",\"counter\":46,\"time\":46,\"type\":\"动漫\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"萌系\",\"counter\":92,\"time\":92,\"type\":\"动漫\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"警匪\",\"counter\":134,\"time\":134,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"内地\",\"category\":\"武侠\",\"counter\":6,\"time\":6,\"type\":\"电影\",\"uid\":\"\"},{\"ID\":\"\",\"area\":\"欧美\",\"category\":\"推理\",\"counter\":51,\"time\":51,\"type\":\"动漫\",\"uid\":\"\"}]";
		// JsonFactory f = new JsonFactory();
		// JsonParser jp = f.createJsonParser(json);

		// BufferedReader br = new BufferedReader(new InputStreamReader(
		// new FileInputStream("/root/test.txt")));
		// String line = null;
		// while (null != (line = br.readLine())) {
		// json = line;
		// }
		System.out.println(json);
		List result = MAPPER.readValue(json, new TypeReference<List>() {
		});
		System.out.println(result);
		for (Object o : result) {
			LinkedHashMap m = (LinkedHashMap) o;
			System.out.println((String) m.get("area") + "\t"
					+ m.get("category") + "\t" + m.get("type"));
		}
		JsonUtil u = new JsonUtil();
		List<String> list = u.evaluateArray(json, "area,category,type");
		for (String s : list) {
			System.out.println(s);
		}
	}
}
