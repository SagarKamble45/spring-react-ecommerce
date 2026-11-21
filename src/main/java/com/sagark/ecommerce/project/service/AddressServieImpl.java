package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.exceptions.ResourceNotFoundException;
import com.sagark.ecommerce.project.model.Address;
import com.sagark.ecommerce.project.model.User;
import com.sagark.ecommerce.project.paylod.AddressDTO;
import com.sagark.ecommerce.project.repositories.AddressRepository;
import com.sagark.ecommerce.project.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServieImpl implements AddressService {


    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);

    }

    @Override
    public List<AddressDTO> getAddress() {
        List<Address> addressList = addressRepository.findAll();
        List<AddressDTO> addressDTOS = addressList.stream().map(address ->
            modelMapper.map(address, AddressDTO.class)).toList();
        return addressDTOS;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(()-> new ResourceNotFoundException("Address", "addressdId", addressId));

        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressesByUser(User user) {

        List<Address> addressList = user.getAddresses();

        List<AddressDTO> addressDTOS = addressList.stream().map(address ->
                modelMapper.map(address, AddressDTO.class)).toList();
        return addressDTOS;
    }


    @Override
    public AddressDTO updateAddress(
            Long addressId,
            AddressDTO addressDTO,
            User user
    ) {
        // Find address only if it belongs to authenticated user
        Address address = addressRepository
                .findByAddressIdAndUser_UserId(
                        addressId,
                        user.getUserId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address", "addressId", addressId
                        ));

        // Update address details
        address.setFullName(addressDTO.getFullName());
        address.setPhone(addressDTO.getPhone());
        address.setAddress(addressDTO.getAddress());
        address.setCity(addressDTO.getCity());
        address.setPincode(addressDTO.getPincode());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());

        Address updatedAddress = addressRepository.save(address);

        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId, User user) {

        // Find address only if it belongs to authenticated user
        Address address = addressRepository
                .findByAddressIdAndUser_UserId(
                        addressId,
                        user.getUserId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address", "addressId", addressId
                        ));

        // Remove address from authenticated user's collection
        user.getAddresses().removeIf(
                item -> java.util.Objects.equals(
                        item.getAddressId(), addressId
                )
        );

        userRepository.save(user);
        addressRepository.delete(address);

        return "Address deleted successfully with addressId: " + addressId;
    }
}
