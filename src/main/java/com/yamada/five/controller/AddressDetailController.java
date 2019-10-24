package com.yamada.five.controller;

import com.yamada.five.pojo.AddressDetail;
import com.yamada.five.service.AddressDetailService;
import com.yamada.five.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/addressDetail")
public class AddressDetailController {

    @Autowired
    private AddressDetailService addressDetailService;

    @RequestMapping("/building")
    public Object getBuildingByArea(@PathParam("area") Integer area) {
        List<AddressDetail> addressDetailList = addressDetailService.getBuildingByArea(area);
        Set<String> buildingList = addressDetailList.stream().map(AddressDetail::getBuilding).collect(Collectors.toSet());
        return ResultVOUtil.success(buildingList);
    }

    @RequestMapping("/room")
    public Object getRoomByBuilding(@PathParam("building") String building) {
        List<AddressDetail> addressDetailList = addressDetailService.getRoomByBuilding(building);
        Map<Long, String> map = addressDetailList.stream().collect(Collectors.toMap(AddressDetail::getAddressDetailId, AddressDetail::getRoom));
        return ResultVOUtil.success(map);
    }
}
