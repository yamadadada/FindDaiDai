package com.yamada.five.service;

import com.yamada.five.dto.AddressDetailDTO;
import com.yamada.five.pojo.AddressDetail;

import java.util.List;

public interface AddressDetailService {

    List<AddressDetail> getBuildingByArea(Integer area);

    List<AddressDetail> getRoomByBuilding(String building);

    AddressDetailDTO addressDetailToAddressDetailDTO(AddressDetail addressDetail);
}
