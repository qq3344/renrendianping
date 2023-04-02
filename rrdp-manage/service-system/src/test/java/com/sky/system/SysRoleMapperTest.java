package com.sky.system;

import com.sky.model.system.SysRole;
import com.sky.system.service.SysRoleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**

 * @version 1.0
 * @time 2022/12/5
 */
@SpringBootTest
public class SysRoleMapperTest {

    @Resource
    private SysRoleService sysRoleService;

    @Test
    public void findAll(){
        List<SysRole> sysRoleList = sysRoleService.list();
        sysRoleList.forEach(System.out::println);
    }
    
    @Test
    public void deleteById(){
        boolean b = sysRoleService.removeById(1L);
    }
}
