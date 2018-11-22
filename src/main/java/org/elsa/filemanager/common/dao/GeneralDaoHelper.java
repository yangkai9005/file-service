package org.elsa.filemanager.common.dao;

import org.apache.commons.lang3.StringUtils;
import org.elsa.filemanager.common.pojo.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * 通用DAO层
 *
 * @author valord577
 */
@Component
public class GeneralDaoHelper {

    private final GeneralDaoHandler handleDao;

    @Autowired
    public GeneralDaoHelper(GeneralDaoHandler handleDao) {
        this.handleDao = handleDao;
    }

    /**
     * 当id为空的时候add记录 当id不为空的时候update
     */
    public void save(Object object) {
        if (null == object) {
            throw new RuntimeException("Entity can't be null.");
        }

        Class<?> clazz = object.getClass();
        String getMethodName = "getId";
        Method getMethod;
        try {
            getMethod = clazz.getMethod(getMethodName);
            Object value = getMethod.invoke(object);
            if (value == null) {
                add(object);
            } else {
                update(object);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 传入对象,保存进数据库 对象和表名的关系ClothFlaw 对应 表 clothFlaw 第一个字母变小写,如果表名不同,需要指定表名
     * 插入的列名和要对象的属性名称一样
     *
     * @param object 待保存的实体
     */
    public void add(Object object) {
        add(object, null);
    }

    /**
     * 传入对象,保存进数据库 对象和表名的关系ClothFlaw 对应 表 clothFlaw 第一个字母变小写,如果表名不同,需要指定表名
     * 插入的列名和要对象的属性名称一样
     *
     * @param object    待添加的实体
     * @param tableName 表名
     */
    public void add(Object object, String tableName) {
        if (null == object) {
            throw new RuntimeException("Entity can't be null.");
        }

        Map<String, Object> para = new HashMap<>(16);

        // 表名为空,按照默认规则生成表名
        if (StringUtils.isBlank(tableName)) {
            tableName = generateTableName(object);
        }
        para.put("tableName", tableName);

        /* 处理entity每个字段 */
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        List<Property> pros = new ArrayList<>();
        para.put("properties", pros);

        Field idField = null;
        Property property;

        for (Field field : fields) {
            // 获取字段对应的value
            String name = field.getName();
            if ("start".equalsIgnoreCase(name) || "limit".equalsIgnoreCase(name)) {
                continue;
            }

            if ("id".equalsIgnoreCase(name)) {
                idField = field;
                continue;
            }

            String firstLetter = name.substring(0, 1).toUpperCase();
            String getMethodName = "get" + firstLetter + name.substring(1);

            try {
                Method getMethod = clazz.getMethod(getMethodName);
                Object value = getMethod.invoke(object);
                // value为空或者不是基础数据
                if (value == null || isNotBasicType(value)) {
                    continue;
                }

                property = new Property();
                property.setName(name);
                property.setValue(value);
                pros.add(property);
            } catch (Exception ignored) {
                // 跳过没有get的属性
            }
        }

        // 调用mybatis
        handleDao.add(para);

        // 设置回Id
        try {
            if (idField != null) {
                String setMethodName = "setId";
                Method setMethod = clazz.getMethod(setMethodName, idField.getType());
                try {
                    // int主键
                    setMethod.invoke(object, para.get("id"));
                } catch (Exception e) {
                    // long主键
                    setMethod.invoke(object, ((Integer) para.get("id")).longValue());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量插入
     *
     * @param oList 待保存的实体列表
     */
    public <T> void addList(List<T> oList) {
        addList(oList, null);
    }

    /**
     * 批量插入
     *
     * @param oList     待保存的实体列表
     * @param tableName 表名
     */
    public <T> void addList(List<T> oList, String tableName) {
        if (null == oList || 0 == oList.size()) {
            throw new RuntimeException("List can't be blank.");
        }

        Map<String, Object> para = new HashMap<>(16);

        T o = oList.get(0);
        // 获取表名
        if (StringUtils.isBlank(tableName)) {
            tableName = generateTableName(o);
        }
        para.put("tableName", tableName);

        List<String> colName = new ArrayList<>();
        para.put("colName", colName);
        // 遍历字段名
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 获取字段对应的value
            String name = field.getName();
            if ("id".equalsIgnoreCase(name) || "start".equalsIgnoreCase(name) || "limit".equalsIgnoreCase(name)) {
                continue;
            }
            colName.add(name);
        }

        List<List<Object>> valueList = new ArrayList<>();
        para.put("valueList", valueList);
        // 根据字段名 查询字段的值
        List<Object> values;
        // 对list循环
        for (T object : oList) {
            clazz = object.getClass();
            values = new ArrayList<>();

            // 对list内 字段循环
            for (String col : colName) {
                String firstLetter = col.substring(0, 1).toUpperCase();
                String getMethodName = "get" + firstLetter + col.substring(1);

                try {
                    Method getMethod = clazz.getMethod(getMethodName);
                    Object value = getMethod.invoke(object);

                    values.add(value);
                } catch (Exception ignored) {
                    // 跳过没有get的属性
                }
            }

            valueList.add(values);
        }

        handleDao.addList(para);
    }

    /**
     * 根据id字段update
     */
    public void update(Object object) {
        update(object, "id");
    }

    /**
     * 通用的更新 对象和表名的关系ClothFlaw 对应 表 clothFlaw 第一个字母变小写,如果表名不同,需要指定表名
     * 更新的列名和要对象的属性名称一样
     *
     * @param object 待更新的实体
     * @param idName 根据idName进行更新 , 即where idName = xxx idName必须为object的一个属性
     */
    public void update(Object object, String idName) {
        update(object, null, idName);
    }

    /**
     * 通用的更新 对象和表名的关系ClothFlaw 对应 表 clothFlaw 第一个字母变小写,如果表名不同,需要指定表名
     * 更新的列名和要对象的属性名称一样
     *
     * @param object    待更新的实体
     * @param tableName 表名
     * @param idName    根据idName进行更新 , 即where idName = xxx idName必须为object的一个属性
     */
    public void update(Object object, String tableName, String idName) {
        if (object == null || StringUtils.isBlank(idName)) {
            throw new RuntimeException("Entity or idName can't be null or blank.");
        }

        Class<?> clazz = object.getClass();

        try {
            clazz.getDeclaredField(idName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("No such field: " + idName + " in " + clazz.getName());
        }
        Map<String, Object> para = new HashMap<>(16);
        para.put("idName", idName);


        // 表名为空,按照默认规则生成表名
        if (StringUtils.isBlank(tableName)) {
            tableName = generateTableName(object);
        }
        para.put("tableName", tableName);

        Field[] fields = clazz.getDeclaredFields();
        List<Property> pros = new ArrayList<>();
        para.put("properties", pros);

        Property property;

        // 设置更新字段
        for (Field field : fields) {
            // 获取字段对应的value
            String name = field.getName();
            String firstLetter = name.substring(0, 1).toUpperCase();
            String getMethodName = "get" + firstLetter + name.substring(1);

            try {
                Method getMethod = clazz.getMethod(getMethodName);
                Object value = getMethod.invoke(object);

                if (value == null || isNotBasicType(value)) {
                    continue;
                }
                // idName对应的值
                if (idName.equalsIgnoreCase(name)) {
                    para.put("idValue", value);
                }

                property = new Property();
                property.setName(name);
                property.setValue(value);
                pros.add(property);
            } catch (Exception e) {
                // 跳过没有get的属性
            }

        }

        handleDao.update(para);
    }

    /**
     * 根据id查询单条记录
     */
    public <T> T quickQueryOneById(Class<T> clazz, Object value) {
        return quickQueryOne(clazz, "id", value);
    }

    /**
     * 快速查询只有一个查询参数的对象
     *
     * @param clazz 待查询的实体类型
     * @param name  参数名
     * @param value 参数值
     * @return 单条结果
     */
    public <T> T quickQueryOne(Class<T> clazz, String name, Object value) {
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("Field name can't be blank.");
        }

        if (null == value) {
            throw new RuntimeException("Field value can't be null.");
        }

        try {
            T object = clazz.newInstance();
            Field field = clazz.getDeclaredField(name);
            String fieldName = field.getName();
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String setMethodName = "set" + firstLetter + fieldName.substring(1);
            Method method = clazz.getDeclaredMethod(setMethodName, field.getType());
            method.invoke(object, value);

            return queryOne(object, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通用的查询 对象和表名的关系ClothFlaw 对应 表 clothFlaw
     * 第一个字母变小写 不需要指定表名
     */
    public <T> T queryOne(T object, String idName) {
        return queryOne(object, null, idName);
    }

    /**
     * 通用的查询 对象和表名的关系ClothFlaw 对应 表 clothFlaw
     * 第一个字母变小写, 如果表名不同, 需要指定表名
     *
     * @param object    待查询的实体
     * @param tableName 表名
     * @param idName    查询条件名称,根据idName进行查询, dName必须为object的一个属性
     */
    public <T> T queryOne(T object, String tableName, String idName) {
        if (object == null || StringUtils.isBlank(idName)) {
            throw new RuntimeException("Entity or idName can't be null or blank.");
        }

        Map<String, Object> para = new HashMap<>(16);

        // 表名为空,按照默认规则生成表名
        if (StringUtils.isBlank(tableName)) {
            tableName = generateTableName(object);
        }
        para.put("tableName", tableName);
        para.put("idName", idName);

        Class<?> clazz = object.getClass();

        Field field;
        try {
            field = clazz.getDeclaredField(idName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("No such field: " + idName + " in " + clazz.getName());
        }

        String name = field.getName();
        String firstLetter = name.substring(0, 1).toUpperCase();
        String getMethodName = "get" + firstLetter + name.substring(1);

        try {
            Method getMethod = clazz.getMethod(getMethodName);
            Object idValue = getMethod.invoke(object);

            para.put("idValue", idValue);
            Map<String, Object> map = handleDao.queryOne(para);
            if (null == map) {
                return null;
            }
            return packResultMap(object, clazz, map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据id删除
     */
    public <T> void quickDeleteById(Class<T> clazz, Object value) {
        quickDelete(clazz, "id", value);
    }

    /**
     * 删除
     *
     * @param clazz object.class
     * @param name  属性名
     * @param value 属性值
     */
    public <T> void quickDelete(Class<T> clazz, String name, Object value) {
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("Field name can't be blank.");
        }

        if (null == value) {
            throw new RuntimeException("Field value can't be null.");
        }

        try {
            T object = clazz.newInstance();
            Field field = clazz.getDeclaredField(name);
            String fieldName = field.getName();
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String setMethodName = "set" + firstLetter + fieldName.substring(1);
            Method method = clazz.getDeclaredMethod(setMethodName, field.getType());
            method.invoke(object, value);

            delete(object, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通用的删除 对象和表名的关系ClothFlaw 对应 表 clothFlaw
     * 第一个字母变小写 不需要指定表名
     */
    public void delete(Object object, String idName) {
        delete(object, null, idName);
    }

    /**
     * 通用的删除 对象和表名的关系ClothFlaw 对应 表 clothFlaw
     * 第一个字母变小写, 如果表名不同, 需要指定表名
     *
     * @param object    待删除的实体
     * @param tableName 表名
     * @param idName    删除条件名称,根据idName进行删除, dName必须为object的一个属性
     */
    public void delete(Object object, String tableName, String idName) {
        if (object == null || StringUtils.isBlank(idName)) {
            throw new RuntimeException("Entity or idName can't be null or blank.");
        }

        Map<String, Object> para = new HashMap<>(16);

        // 表名为空,按照默认规则生成表名
        if (StringUtils.isBlank(tableName)) {
            tableName = generateTableName(object);
        }
        para.put("tableName", tableName);
        para.put("idName", idName);

        Class<?> clazz = object.getClass();

        Field field;
        try {
            field = clazz.getDeclaredField(idName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("No such field: " + idName + " in " + clazz.getName());
        }

        String name = field.getName();
        String firstLetter = name.substring(0, 1).toUpperCase();
        String getMethodName = "get" + firstLetter + name.substring(1);

        try {
            Method getMethod = clazz.getMethod(getMethodName);
            Object idValue = getMethod.invoke(object);

            para.put("idValue", idValue);
            handleDao.delete(para);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 默认字段查询 默认表名
     *
     * @return list
     */
    public <T> List<T> query(T object, Integer start, Integer limit) {
        return query(object, null, start, limit);
    }

    /**
     * 默认字段查询 默认表名
     *
     * @return count
     */
    public <T> int queryCount(T object) {
        return queryCount(object, null);
    }

    /**
     * 默认字段查询 指定表名
     *
     * @return list
     */
    public <T> List<T> query(T object, String tableName, Integer start, Integer limit) {
        return query(object, tableName, null, null, start, limit);
    }

    /**
     * 默认字段查询 指定表名
     *
     * @return count
     */
    public <T> int queryCount(T object, String tableName) {
        return queryCount(object, tableName, null, null);
    }

    /**
     * 默认表名 使用like条件查询
     *
     * @return list
     */
    public <T> List<T> queryUsingLike(T object, List<String> likeProperties, Integer start, Integer limit) {
        return query(object, likeProperties, null, start, limit);
    }

    /**
     * 默认表名 使用like条件查询
     *
     * @return count
     */
    public <T> int queryCountUsingLike(T object, String tableName, List<String> likeProperties) {
        return queryCount(object, tableName, likeProperties, null);
    }

    /**
     * 默认表名 使用自定义条件查询
     *
     * @return list
     */
    public <T> List<T> queryCustomize(T object, List<String> userPara, Integer start, Integer limit) {
        return query(object, null, userPara, start, limit);
    }

    /**
     * 默认表名 使用自定义条件查询
     *
     * @return count
     */
    public <T> int queryCountCustomize(T object, String tableName, List<String> userPara) {
        return queryCount(object, tableName, null, userPara);
    }

    /**
     * 默认表名 自定义额外查询条件
     *
     * @return list
     */
    public <T> List<T> query(T object, List<String> likeProperties, List<String> userPara, Integer start, Integer limit) {
        return query(object, null, likeProperties, userPara, start, limit);
    }

    /**
     * 通用的分页查询 根据object中的不为空的属性进行查询 对象和表名的关系ClothFlaw 对应 表 clothFlaw
     * 第一个字母变小写,如果表名不同,需要指定表名 支持like查询方式,需要like查询的属性以likeProperties字符串链表的方式传入,
     * 传入的属性要在object中存在 支持自定义sql片段查询.
     * List<String> userPara = new ArrayList<>();
     * userPara.add("and name like '%time%'");
     * <p>
     * 默认使用主键id 作为子查询
     *
     * @param object         等值查询条件
     * @param tableName      表名
     * @param likeProperties 相似查询条件
     * @param start          页码          默认第一页
     * @param limit          每页数据量     默认每页10条数据
     */
    public <T> List<T> query(T object, String tableName, List<String> likeProperties, List<String> userPara, Integer start, Integer limit) {
        return query(object, tableName, likeProperties, userPara, "id", start, limit);
    }

    /**
     * 通用的分页查询 根据object中的不为空的属性进行查询 对象和表名的关系ClothFlaw 对应 表 clothFlaw
     * 第一个字母变小写,如果表名不同,需要指定表名 支持like查询方式,需要like查询的属性以likeProperties字符串链表的方式传入,
     * 传入的属性要在object中存在 支持自定义sql片段查询.
     * List<String> userPara = new ArrayList<>();
     * userPara.add("and name like '%time%'");
     * <p>
     * 带分页的查询 start,limit
     *
     * @param object         等值查询条件
     * @param tableName      表名
     * @param likeProperties 相似查询条件
     * @param primaryKey     主键          一般为id
     * @param start          页码          默认第一页
     * @param limit          每页数据量     默认每页10条数据
     */
    public <T> List<T> query(T object, String tableName, List<String> likeProperties, List<String> userPara, String primaryKey, Integer start, Integer limit) {
        if (null == object) {
            throw new RuntimeException("Entity can't be null.");
        }

        if (null == primaryKey) {
            throw new RuntimeException("Primary key can't be null.");
        }

        Class<?> clazz = object.getClass();

        // 添加主键 作为子查询使用
        try {
            clazz.getDeclaredField(primaryKey);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("No such field: " + primaryKey + " in " + clazz.getName());
        }
        Map<String, Object> para = new HashMap<>(16);
        para.put("primaryKey", primaryKey);

        if (null == start) {
            start = 1;
        }

        if (null == limit) {
            limit = 10;
        }

        if (start < 1 || limit < 1) {
            throw new RuntimeException("Error params of pages.");
        }

        List<T> list = new ArrayList<>();

        // 用户自己的sql语句片段
        para.put("userPara", userPara);
        para.put("start", (start - 1) * limit);
        para.put("limit", limit);

        // 表名为空,按照默认规则生成表名
        if (StringUtils.isBlank(tableName)) {
            tableName = generateTableName(object);
        }
        para.put("tableName", tableName);


        // 普通属性
        List<Property> pros = new ArrayList<>();
        // like属性
        List<Property> likePros = new ArrayList<>();
        para.put("properties", pros);
        para.put("likeProperties", likePros);

        /* 对每个字段做处理 */
        packParasForQuery(object, clazz, likeProperties, likePros, pros);
        // 执行查询
        List<Map<String, Object>> result = handleDao.query(para);

        // 查询结果封装为Object
        for (Map<String, Object> map : result) {
            list.add(packResultMap(object, clazz, map));
        }

        return list;
    }

    /**
     * 数据量大大时候 查询列表size 十分消耗性能
     * 所以分开实现 前端列表尽可能少调用该方法
     */
    public <T> int queryCount(T object, String tableName, List<String> likeProperties, List<String> userPara) {
        if (null == object) {
            throw new RuntimeException("Entity can't be null.");
        }

        // 用户自己的sql语句片段
        Map<String, Object> para = new HashMap<>(16);
        para.put("userPara", userPara);

        // 表名为空,按照默认规则生成表名
        if (StringUtils.isBlank(tableName)) {
            tableName = generateTableName(object);
        }
        para.put("tableName", tableName);

        // 普通属性
        List<Property> pros = new ArrayList<>();
        // like属性
        List<Property> likePros = new ArrayList<>();
        para.put("properties", pros);
        para.put("likeProperties", likePros);

        /* 对每个字段做处理 */
        Class<?> clazz = object.getClass();
        packParasForQuery(object, clazz, likeProperties, likePros, pros);

        return handleDao.queryCount(para);
    }

    /**
     * query专用
     * 查询条件para 组装
     */
    private static <T> void packParasForQuery(T object, Class<?> clazz, List<String> likeProperties, List<Property> likePros, List<Property> pros) {
        Field[] fields = clazz.getDeclaredFields();
        Property property;
        for (Field field : fields) {
            String name = field.getName();
            // 跳过id属性 主键查询应使用queryOne()方法
            if ("id".equalsIgnoreCase(name)) {
                continue;
            }

            try {
                // 因为mybatis映射Object里的list时用的是get方法，而如果get得到一个null会报空指针，所以全都返回一个空list
                // 这里会产生冲突，所以过滤掉list类型属性
                Type type = field.getType();
                if (StringUtils.equals(type.toString(), "interface java.util.List")) {
                    continue;
                }
                String firstLetter = name.substring(0, 1).toUpperCase();
                String getMethodName = "get" + firstLetter + name.substring(1);

                Method getMethod = clazz.getMethod(getMethodName);
                Object value = getMethod.invoke(object);

                if (value == null || isNotBasicType(value)) {
                    continue;
                }

                property = new Property();
                property.setName(name);
                property.setValue(value);

                if (null == likeProperties) {
                    pros.add(property);
                } else {
                    if (likeProperties.contains(name)) {
                        likePros.add(property);
                    } else {
                        pros.add(property);
                    }
                }
            } catch (Exception e) {
                // 跳过没有get的属性
            }
        }
    }

    /**
     * query专用
     * 查询结果map 组装
     */
    @SuppressWarnings("unchecked")
    private static <T> T packResultMap(T object, Class<?> clazz, Map<String, Object> map) {
        T resultObject = null;
        try {
            resultObject = (T) object.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // 由于key value都需要使用 采用entrySet()遍历
        // keySet()循环时 map.get(key)进行hash计算需要耗时
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                Object value = entry.getValue();
                // 值是空,则不映射到对象
                if (value == null || isNotBasicType(value)) {
                    continue;
                }

                Field field = clazz.getDeclaredField(entry.getKey());
                if (field == null) {
                    continue;
                }

                String name = field.getName();
                String firstLetter = name.substring(0, 1).toUpperCase();
                String setMethodName = "set" + firstLetter + name.substring(1);
                Method method = clazz.getDeclaredMethod(setMethodName, field.getType());

                // 设置entity的字段值
                if (field.getType().isEnum()) {
                    // 枚举类型
                    Object objects[] = field.getType().getEnumConstants();
                    for (Object o : objects) {
                        if (value.toString().equalsIgnoreCase(o.toString())) {
                            method.invoke(resultObject, o);
                        }
                    }

                } else if (field.getType().toString().contains("Boolean")) {
                    if ((int) value == 0) {
                        method.invoke(resultObject, Boolean.FALSE);
                    } else {
                        method.invoke(resultObject, Boolean.TRUE);
                    }
                } else {
                    method.invoke(resultObject, value);
                }

            } catch (Exception ignored) {
                // 跳过没有set的属性
            }
        }

        return resultObject;
    }

    /**
     * 根据class名称生成表明
     * 如 EntityExample.java 则生成 entityExample
     */
    public String generateTableName(Object object) {
        if (null == object) {
            throw new RuntimeException("Entity can't be null.");
        }

        String className = object.getClass().getName();
        String tableName = className.substring(className.lastIndexOf(".") + 1);
        char[] chs = tableName.toCharArray();
        if (chs.length > 0) {
            chs[0] = Character.toLowerCase(chs[0]);
        }

        return new String(chs);
    }

    /**
     * 判断是否为基础类型
     */
    private static boolean isNotBasicType(Object o) {
        return !(o instanceof String) && !(o instanceof Integer) && !(o instanceof Long) && !(o instanceof Date) && !(o instanceof Double) && !(o instanceof Enum) && !(o instanceof Boolean) && !(o instanceof BigDecimal);
    }
}
