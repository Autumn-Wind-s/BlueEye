package com.sqlInterceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;

import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;


@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
public class SqlExecuteTimeCountInterceptor implements Interceptor {
    /**
     * 拦截的sql集合，若为空则拦截所有
     */
    private HashSet<String> sqls = new HashSet<>();


    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object target = invocation.getTarget();
        long startTime = System.currentTimeMillis();
        StatementHandler statementHandler = (StatementHandler) target;
        ResultHandler r= (ResultHandler) target;
        try {
            return invocation.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long timeCount = endTime - startTime;
            BoundSql boundSql = statementHandler.getBoundSql();
            String sql = boundSql.getSql();
            if (sqls.isEmpty()) {
                //集合为空，无需判断，直接将拦截的sql的执行数据上传到BlueEye
                System.out.println("executing SQL:" + sql + "time:" + timeCount);
            } else if (sqls.contains(sql)) {
                //集合不为空且当前sql在集合中将拦截的sql的执行数据上传到BlueEye
                System.out.println("executing SQL:" + sql + "time:" + timeCount);
            }

        }
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        for (Object value : properties.values()) {
            sqls.add((String) value);
        }
    }


}
