package com.yamada.five.service.Impl;

import com.yamada.five.mapper.AddressMapper;
import com.yamada.five.pojo.Address;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AddressServiceImplTest {

    @Autowired
    private AddressMapper addressMapper;

    @Test
    public void getById() {
    }

    @Test
    public void getByUserId() {
    }

    @Test
    public void insert() {
        Address address = new Address();
        address.setUserId(1L);
        address.setAddressDetailId(2L);
        address.setAddressName("收获人");
        address.setAddressPhone("13105678901");
        Integer result = addressMapper.insert(address);
        log.info("【新增地址】result: " + address.getAddressId());
    }

    @Test
    public void addressToAddressDTO() {
    }

    @Test
    public void addressListToAddressDTOList() {
    }
}