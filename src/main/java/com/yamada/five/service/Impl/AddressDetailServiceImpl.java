package com.yamada.five.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yamada.five.dto.AddressDetailDTO;
import com.yamada.five.enums.AreaEnum;
import com.yamada.five.mapper.AddressDetailMapper;
import com.yamada.five.pojo.AddressDetail;
import com.yamada.five.service.AddressDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressDetailServiceImpl implements AddressDetailService {

    @Autowired
    private AddressDetailMapper addressDetailMapper;

    @Override
    public List<AddressDetail> getBuildingByArea(Integer area) {
        QueryWrapper<AddressDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("area", area).select("building");
        return addressDetailMapper.selectList(wrapper);
    }

    @Override
    public List<AddressDetail> getRoomByBuilding(String building) {
        QueryWrapper<AddressDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("building", building).select("address_detail_id", "room");
        return addressDetailMapper.selectList(wrapper);
    }

    @Override
    public AddressDetailDTO addressDetailToAddressDetailDTO(AddressDetail addressDetail) {
        if (addressDetail == null) {
            return null;
        }
        AddressDetailDTO addressDetailDTO = new AddressDetailDTO();
        BeanUtils.copyProperties(addressDetail, addressDetailDTO);
        for (AreaEnum areaEnum: AreaEnum.values()) {
            if (areaEnum.getCode().equals(addressDetail.getArea())) {
                addressDetailDTO.setAreaEnum(areaEnum);
                break;
            }
        }
        return addressDetailDTO;
    }
}
