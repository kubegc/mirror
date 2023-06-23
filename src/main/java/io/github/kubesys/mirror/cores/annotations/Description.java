/**
 * Copyrigt (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.kubesys.mirror.cores.annotations;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wuheng@iscas.ac.cn
 * @since  1.1.0
 *
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Description {

	/**
	 * @return 具体描述信息
	 */
	String value() default "";
	
	/**
	 * @return 类型
	 */
	String type() default "";
}
