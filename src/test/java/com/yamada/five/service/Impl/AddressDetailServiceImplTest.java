package com.yamada.five.service.Impl;

import com.yamada.five.mapper.AddressDetailMapper;
import com.yamada.five.pojo.AddressDetail;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressDetailServiceImplTest {

    @Autowired
    private AddressDetailMapper mapper;

    @Test
    public void getBuildingByArea() {
    }

    @Test
    public void getRoomByBuilding() {
    }

    @Test
    public void addressDetailToAddressDetailDTO() {
    }

    @Test
    public void updateById() {
        AddressDetail addressDetail = new AddressDetail();
        addressDetail.setAddressDetailId(1L);
        addressDetail.setRoom("605");
        int result = mapper.updateById(addressDetail);
        Assert.assertEquals(1, result);
    }
}