package com.yamada.five.dto;

import com.yamada.five.pojo.User;
import lombok.Data;

@Data
public class AddressDTO {

    private Long addressId;

    private User user;

    private AddressDetailDTO addressDetailDTO;

    private String addressName;

    private String addressPhone;

    public String getAddressString() {
        if (addressDetailDTO != null) {
            return addressDetailDTO.getAreaEnum().getArea() + addressDetailDTO.getBuilding() + addressDetailDTO.getRoom();
        }
        return "";
    }
}
