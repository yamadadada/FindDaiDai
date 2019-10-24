package com.yamada.five.handler;

import com.yamada.five.exception.FiveApiException;
import com.yamada.five.utils.ResultVOUtil;
import com.yamada.five.vo.ResultVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FiveApiExceptionHandler {

    @ExceptionHandler(FiveApiException.class)
    public ResponseEntity<ResultVO> error(FiveApiException e) {
        return ResultVOUtil.error(new ResultVO(500, e.getMessage(), e.getData()));
    }
}
