--! qt:dataset:src
set hive.exec.post.hooks=org.apache.hadoop.hive.ql.hooks.VerifyOverriddenConfigsHook;
set some.hive.config.does.not.exit=abc;

select count(*) from src;
