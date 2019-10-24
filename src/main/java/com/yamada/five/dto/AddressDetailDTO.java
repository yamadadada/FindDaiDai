package com.yamada.five.dto;

import com.yamada.five.enums.AreaEnum;
import lombok.Data;

@Data
public class AddressDetailDTO {

    private Long addressDetailId;

    private AreaEnum areaEnum;

    private String building;

    private String room;
}
