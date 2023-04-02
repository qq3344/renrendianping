package com.sky;

import com.sky.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Clazz {
    private String ClazzName;
    @OneSelf
    List<UserDTO> userDTOList;


}
