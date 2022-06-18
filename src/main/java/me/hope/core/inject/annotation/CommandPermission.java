package me.hope.core.inject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermission {
    /**
     * 权限节点
     * @return
     */
    String value();

    /**
     * 是否仅控制台
     * @return
     */
    boolean onlyConsole() default false;

}
