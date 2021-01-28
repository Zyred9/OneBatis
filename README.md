## OneBatis 仿造 MyBatis



#### 注： 该项目仅供于学习，功底有限，代码很 LOW



###  项目说明

- 起因： 在学习了 `mybatis-3` 源码后，觉得 `mybatis` 并没有想象中那么的复杂，于是动手开始自己写一个类似功能的半 `ORM ` 框架，说干就干。
- 经过：中途遇到很多困扰的问题，有很多地方并不是按照 `mybatis` 一一对应做的，很多地方加入了自己的理解，例如解析 #{}占位符的逻辑等。
- 结果： 耗时两个月时间，摸爬滚打瞎写，算是能执行 `增删改查` 的 `SQL` 语句，并且能够完成参数的自动注入。

### 项目缺陷

- 有一说一，本人功底菜得一批，代码缺陷非常大，等后续功底成熟了，一步步的优化吧。

- 和 `mybatis` 比起来，该项目缺少 `${}` 占位符的功能，且只 `xml` 文件中只支持四种标签 `<insert> <select> <update> <delete>` ，其他的标签暂时没有做，在全局配置文件 `mybatis-config.xml` 中，也只支持少量的标签，可以在本项目中 `resource` 获得 `mybatis-config.xml` 也可以通过下面描述中获得。

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
  
      <properties resource="onebatis.properties"></properties>
      <settings>
          <!-- 打印查询语句 -->
          <setting name="logImpl" value="STDOUT_LOGGING" />
          <!-- 控制全局缓存（二级缓存），默认 true-->
          <setting name="cacheEnabled" value="false"/>
          <!-- 延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。默认 false  -->
          <setting name="lazyLoadingEnabled" value="false"/>
      </settings>
  
      <environments default="development">
          <environment id="development">
              <transactionManager type="JDBC"/>
              <dataSource type="POOLED">
                  <property name="driver" value="${jdbc.driver}"/>
                  <property name="url" value="${jdbc.url}"/>
                  <property name="username" value="${jdbc.username}"/>
                  <property name="password" value="${jdbc.password}"/>
              </dataSource>
          </environment>
      </environments>
  
      <mappers>
          <mapper resource="UserMapper.xml"/>
      </mappers>
  
  </configuration>
  ```

  

- 项目中不支持一级缓存和二级缓存，所以 `mybatis-config.xml` 下 `<settings> -> cacheEnabled / lazyLoadingEnabled / logImpl ` 不可用 。

### 快速开始

- 下载源码:  https://github.com/Zyred9/OneBatis

- 使用 `maven` 安装 `jar` 到 本地 `maven` 仓库

  ```shell
  mvn install
  ```

- 新建项目，引入依赖

  ```xml
  <dependencies>
      <!-- 添加 mysql连接依赖 -->
      <dependency>
          <groupId>mysql</groupId>
          <artifactId>mysql-connector-java</artifactId>
          <version>8.0.21</version>
      </dependency>
  
      <!-- 添加 onebatis 依赖 -->
      <dependency>
          <groupId>com.example</groupId>
          <artifactId>OneBatis</artifactId>
          <version>0.0.1-snapshot</version>
      </dependency>
      
      <dependency>
          <groupId>junit</groupId>
          <artifactId>junit</artifactId>
          <version>4.12</version>
          <scope>compile</scope>
      </dependency>
      
  </dependencies>
  ```

  

- 新建 全局配置文件 `mybatis-config.xml` ，由于上面存在，这里不再重复
- 新建 `db.properties`

```properties
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/onebatis?serverTimezone=GMT%2B8
jdbc.username=root
jdbc.password=root
cache.enabled=true
connection.size=2
```

- 新建数据库 `onebatis ` , 表 `t_user`

  ```sql
  CREATE TABLE `t_user` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_name` varchar(10) DEFAULT NULL,
    `password` varchar(32) DEFAULT NULL,
    `address` varchar(100) DEFAULT NULL,
    `phone` varchar(11) DEFAULT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
  
  INSERT INTO onebatis.t_user (user_name,password,address,phone) VALUES
  ('张三','12346','北京','1008611');
  INSERT INTO onebatis.t_user (user_name,password,address,phone) VALUES
  ('李四','12346','上海','1008612');
  INSERT INTO onebatis.t_user (user_name,password,address,phone) VALUES
  ('王五','12346','广东','1008613');
  INSERT INTO onebatis.t_user (user_name,password,address,phone) VALUES
  ('赵六','12346','杭州','1008614');
  ```

- 新建 `User` 对象

  ```java
  public class User implements Serializable {
  
      private String id;
      private String userName;
      private String password;
      private String address;
      private String phone;
      ... setter getter ...
  }
  ```

- 新建 `UserMapper.java`

  ```java
  public interface UserMapper {
      List<User> getUserPage(String phone);
  }
  ```

- 新建 `UserMapper.xml`

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="com.example.onebatis.UserMapper">
  
      <select id="getUserPage" resultType="com.example.onebatis.User">
          SELECT id, user_name, password, address, phone FROM t_user where phone = #{phone}
      </select>
  
  </mapper>
  ```

- 新建 `OneBatisTest.java`

  ```java
  public class OneBatisTest {
  
      private SqlSessionFactory build;
  
      @Before
      public void before() {
          // 读取配置文件，使用 hutool 完成的
          InputStream stream = Resources.getResourceAsStream("classpath:mybatis-config.xml");
          // 通过配置文件获取 SqlSessionFactory，放入成员变量中，提供后续事件
          build = new SqlSessionFactoryBuilder().build(stream);
      }
  
      @Test
      public void selectList() {
          SqlSession sqlSession = build.openSession();
          UserMapper mapper = sqlSession.getMapper(UserMapper.class);
          List<User> page = mapper.getUserPage("10086");
          page.stream().forEach(System.out::println);
      }
  }
  ```

- 执行结果

  ```text
  User{id='1', userName='张三', password='12346', address='北京', phone='10086'}
  User{id='2', userName='李四', password='12346', address='上海', phone='10086'}
  User{id='3', userName='王五', password='12346', address='广东', phone='10086'}
  User{id='4', userName='赵六', password='12346', address='杭州', phone='10086'}
  ```

### 插件使用

- 实现 `Interceptor` 接口

```java
@Interceptors(
        @Signature(
                type = Executor.class,
                method = "query",
            	// 这里的参数一定要和 Executor#query() 顺序和个数保持一致，在扫描阶段才能将其放入到 Invocation 对象中保存
                args = {Object[].class, SqlBuilder.class}
        )
)
public class MyInterceptor implements Interceptor {
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
		
        // invocation.getArgs()的个数和注解 @Signature的args 顺序一致，否则会类型转换异常
        SqlBuilder sqlBuilder = (SqlBuilder)invocation.getArgs()[1];

        String sql = sqlBuilder.getSql();
        // 复写sql，拼接分页，注意这里的第一个空格
        String suffix = " limit 2";
        sqlBuilder.setSql(sql.concat(suffix));

        // 执行动态代理过后的方法
        return invocation.proceed();
    }
}
```

- 修改 `mybatis-config.xml` 文件，在 `plugin` 标签处新增一下内容

```xml
<plugins>
    <plugin interceptor="com.example.onebatis.MyInterceptor">
        <property name="onebatis" value="batis" />
    </plugin>
</plugins>
```

- 运行插件，得到结果（注意，此时我的数据库中有4条数据）

```txt
User{id='1', userName='张三', password='12346', address='北京', phone='10086'}
User{id='2', userName='李四', password='12346', address='上海', phone='10086'}
```

#### mybatis-3 中文注释版地址

- https://github.com/Zyred9/mybatis-3

#### 博客地址

- https://blog.csdn.net/qq_38800175