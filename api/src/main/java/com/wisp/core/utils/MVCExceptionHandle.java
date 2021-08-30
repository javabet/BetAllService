package com.wisp.core.utils;

import com.wisp.core.service.ResponseResult;
import com.wisp.core.utils.exception.BusinessCommonException;
import com.wisp.core.web.base.BaseController;
import com.wisp.core.vo.ResponseResultVo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.List;
import java.util.Set;

/**
 * mvc异常工具
 *
 * @author Fe 2016年5月16日
 */
public class MVCExceptionHandle {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    //捕捉验证失败
    /**
    @ExceptionHandler(value = ConstraintViolationException.class)
    public Object handleMethodArgumentNotValidException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();
            // 读取参数字段，constraintViolation.getMessage() 读取验证注解中的message值
            String paramName = pathImpl.getLeafNode().getName();
            String message = "参数{".concat(paramName).concat("}").concat(constraintViolation.getMessage());
            logger.info("{} -> {} -> {}", constraintViolation.getRootBeanClass().getName(), pathImpl.toString(), message);
            return ResponseResultVo.failure(1,message);
        }
        return ResponseResultVo.success();
    }
    **/

    /**
     * 捕捉异常
     *
     * @param e
     * @return
     */

    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletResponse response, Exception e) {
        String traceId = TraceIdUtils.getTraceId();
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException me = (MethodArgumentNotValidException) e;
            StringBuilder sb = new StringBuilder();
            List<ObjectError> errors = me.getBindingResult().getAllErrors();
            if (errors.size() > 1) {
                sb.append("共" + errors.size() + "个错误；");
            }
            for (ObjectError error : errors) {
                sb.append(error.getDefaultMessage()).append("，");
            }
            sb.delete(sb.length() - 1, sb.length());
            logger.warn("参数非法 param={} ex={} traceId={}", sb.toString(), ExceptionUtils.getStackTrace(e), traceId);
            return ResponseResultVo.failure(400,sb.toString());
        }  else if (e instanceof HttpMessageNotReadableException) {
            HttpMessageNotReadableException le = (HttpMessageNotReadableException) e;
            logger.error("请传入body ex={} traceId={}", ExceptionUtils.getStackTrace(le), traceId);
            return new ResponseResultVo(400, "请传入body");
        } else if (e instanceof BusinessCommonException) {
            BusinessCommonException le = (BusinessCommonException) e;
            logger.error("业务异常 ex={} traceId={}", ExceptionUtils.getStackTrace(le), traceId);
            return new ResponseResultVo(le.getCode(), le.getMsg());
        }
        else if( e instanceof BindException)
        {
            BindException le = (BindException) e;
            logger.error("业务异常 ex={} traceId={}", ExceptionUtils.getStackTrace(le), traceId);
            String message = "参数{".concat("..").concat("}").concat("afe");
            return new ResponseResultVo(500,message );
        }
        else if (e instanceof ConstraintViolationException) {
            List<String> list = BeanValidators.extractMessage((ConstraintViolationException) e);
            logger.error("ex={}, traceId={}", list.toString() + ExceptionUtils.getStackTrace(e), traceId);
            return new ResponseResultVo(400, list.toString());
        }
        else if( e instanceof  MissingServletRequestParameterException)
        {
            MissingServletRequestParameterException le = (MissingServletRequestParameterException) e;
            StringBuilder sb = new StringBuilder();
            sb.append("参数缺失:").append(le.getParameterName());
            return new ResponseResultVo(400, sb.toString());
        }
        else {
            logger.error("ex={}, traceId={}", ExceptionUtils.getStackTrace(e), traceId);
            return new ResponseResultVo(500, "系统异常");
        }

    }

}
