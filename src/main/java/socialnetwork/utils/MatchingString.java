package socialnetwork.utils;

import socialnetwork.domain.UserDTO;
import socialnetwork.domain.getter.Getter;
import socialnetwork.domain.getter.GetterUserDTO;

import java.util.List;
import java.util.stream.Collectors;

public class MatchingString {
    public static List<UserDTO> getListUserDTOMatching(List<UserDTO> userDTOList, String property, String matchingString) {
        Getter getterUserDTO = new GetterUserDTO();
        return userDTOList
                .stream()
                .filter(userDTO -> getterUserDTO.get(userDTO, property).toUpperCase().startsWith(matchingString.toUpperCase()))
                .collect(Collectors.toList());
    }
}
