package com.yamada.five.controller;

import com.yamada.five.bo.UserInfo;
import com.yamada.five.dto.AddressDTO;
import com.yamada.five.enums.AreaEnum;
import com.yamada.five.enums.ResultEnums;
import com.yamada.five.exception.FiveException;
import com.yamada.five.pojo.Address;
import com.yamada.five.pojo.AddressDetail;
import com.yamada.five.pojo.User;
import com.yamada.five.service.AddressDetailService;
import com.yamada.five.service.AddressService;
import com.yamada.five.service.UserService;
import com.yamada.five.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressDetailService addressDetailService;

    @GetMapping("/toAdd")
    public String toAdd(Map<String, Object> map) {
        map.put("areaEnumList", AreaEnum.values());
        return "address/add";
    }

    /**
     * 前往管理收货地址页面
     * @param map
     * @return
     */
    @GetMapping("")
    public String list(Map<String, Object> map) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getById(userInfo.getUserId());
        List<Address> addressList = addressService.getByUserId(user.getUserId());
        List<AddressDTO> addressDTOList = addressService.addressListToAddressDTOList(addressList);
        Long defaultAddressId = user.getAddressId();
        map.put("defaultAddressId", defaultAddressId);
        map.put("addressDTOList", addressDTOList);
        return "address/list";
    }

    /**
     * 新增收货地址
     * @param address
     * @param isDefault
     * @return
     */
    @PostMapping("")
    @ResponseBody
    public Object add(@Valid Address address, @RequestParam(value = "isDefault") Boolean isDefault) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        address.setUserId(userInfo.getUserId());
        address.setAddressDetailId(address.getAddressDetailId());
        Long addressId = addressService.insert(address);
        //设置默认地址
        if (isDefault) {
            User user = userService.getById(userInfo.getUserId());
            user.setAddressId(addressId);
            Integer result = userService.updateById(user);
            if (result == 0) {
                throw new FiveException(ResultEnums.USER_UPDATE_ERROR, "/toAdd");
            }
        }
        return ResultVOUtil.success(null);
    }

    /**
     * 修改收货地址
     * @param addressId
     * @param form
     * @param isDefault
     * @return
     */
    @PostMapping("/{addressId}")
    @ResponseBody
    public Object update(@PathVariable("addressId") Long addressId, @Valid Address form,
                         @RequestParam("isDefault") Boolean isDefault) {
        // 验证身份
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Address address = addressService.getById(addressId);
        if (!userInfo.getUserId().equals(address.getUserId())) {
            throw new FiveException(ResultEnums.NOT_AUTHORITY);
        }
        address.setAddressDetailId(form.getAddressDetailId());
        address.setAddressName(form.getAddressName());
        address.setAddressPhone(form.getAddressPhone());
        if (isDefault) {
            User user = userService.getById(userInfo.getUserId());
            user.setAddressId(addressId);
            userService.updateById(user);
        }
        addressService.update(address);
        return ResultVOUtil.success(null);
    }

    @GetMapping("/user/{userId}")
    @ResponseBody
    public Object getAddressByUserId(@PathVariable("userId") Long userId) {
        //检查权限
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userInfo.getUserId().equals(userId)) {
            throw new FiveException(ResultEnums.NOT_AUTHORITY);
        }
        User user = userService.getById(userInfo.getUserId());
        List<Address> addressList = addressService.getByUserId(user.getUserId());
        Long defaultAddressId = user.getAddressId();
        Map<String, Object> map = new HashMap<>();
        if (defaultAddressId != null) {
            Address address = addressService.getById(defaultAddressId);
            AddressDTO defaultAddressDTO = addressService.addressToAddressDTO(address);
            map.put("defaultAddressDTO", defaultAddressDTO);
            //从List中去除默认地址
            addressList = addressList.stream().filter(e -> !(e.getAddressId().equals(defaultAddressId))).collect(Collectors.toList());
        }
        List<AddressDTO> addressDTOList = addressService.addressListToAddressDTOList(addressList);
        map.put("addressDTOList", addressDTOList);
        return ResultVOUtil.success(map);
    }

    /**
     * 查看地址详细信息
     * @param addressId
     * @param map
     * @return
     */
    @GetMapping("/{addressId}")
    public String detail(@PathVariable("addressId") Long addressId, Map<String, Object> map) {
        // 验证身份
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Address address = addressService.getById(addressId);
        if (!address.getUserId().equals(userInfo.getUserId())) {
            throw new FiveException(ResultEnums.NOT_AUTHORITY);
        }
        AddressDTO addressDTO = addressService.addressToAddressDTO(address);
        List<AddressDetail> buildingList = addressDetailService.getBuildingByArea(addressDTO.getAddressDetailDTO().getAreaEnum().getCode());
        List<AddressDetail> roomList = addressDetailService.getRoomByBuilding(addressDTO.getAddressDetailDTO().getBuilding());
        // 默认地址
        User user = userService.getById(userInfo.getUserId());
        Boolean isDefault = addressId.equals(user.getAddressId());
        map.put("addressDTO", addressDTO);
        map.put("buildingList", buildingList);
        map.put("roomList", roomList);
        map.put("isDefault", isDefault);
        map.put("areaEnumList", AreaEnum.values());
        return "address/add";
    }

    @GetMapping("/delete/{addressId}")
    public String delete(@PathVariable("addressId") Long addressId, Map<String, Object> map) {
        // 验证身份
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getById(userInfo.getUserId());
        //如果是默认地址，删除
        if (addressId.equals(user.getAddressId())) {
            user.setAddressId(null);
            Integer result = userService.updateById(user);
            if (result != 1) {
                throw new FiveException(ResultEnums.USER_UPDATE_ERROR);
            }
        }
        addressService.deleteById(addressId);
        map.put("msg", "删除成功！");
        map.put("url", "/address");
        return "common/success";
    }
}
