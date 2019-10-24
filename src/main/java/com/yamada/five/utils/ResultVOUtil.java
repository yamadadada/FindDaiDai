package com.yamada.five.utils;

import com.yamada.five.enums.ResultEnums;
import com.yamada.five.vo.ResultVO;
import org.springframework.http.ResponseEntity;

/**
 * json数据返回异常
 */
public class ResultVOUtil {

    /**
     * 网页异常
     * @param resultVO
     * @return
     */
    public static ResponseEntity<ResultVO> error(ResultVO resultVO) {
        return ResponseEntity.status(resultVO.getCode()).body(resultVO);
    }

    public static ResponseEntity<ResultVO> success(Object object) {
        ResultVO resultVO = new ResultVO(1000, "success", object);
        return ResponseEntity.status(200).body(resultVO);
    }

    /**
     * 业务异常
     * @param resultEnums
     * @return
     */
    public static ResponseEntity<ResultVO> error(ResultEnums resultEnums) {
        ResultVO resultVO = new ResultVO(resultEnums.getCode(), resultEnums.getMsg(), null);
        return ResponseEntity.status(200).body(resultVO);
    }
}
