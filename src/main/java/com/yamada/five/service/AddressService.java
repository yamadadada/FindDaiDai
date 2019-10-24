package com.yamada.five.service;

import com.yamada.five.dto.AddressDTO;
import com.yamada.five.pojo.Address;

import java.util.List;

public interface AddressService {

    Address getById(Long addressId);

    List<Address> getByUserId(Long userId);

    AddressDTO addressToAddressDTO(Address address);

    Long insert(Address address);

    void update(Address address);

    void deleteById(Long addressId);

    List<AddressDTO> addressListToAddressDTOList(List<Address> addressList);
}
