package com.wisp.game.bet.recharge.crud.recharge;

import com.wisp.core.service.CrudService;
import com.wisp.core.utils.type.StringUtils;
import com.wisp.game.bet.recharge.beanConfig.YmlConfig;
import com.wisp.game.bet.recharge.dao.NodeDao;
import com.wisp.game.bet.recharge.dao.RoleDao;
import com.wisp.game.bet.recharge.dao.entity.AdminEntity;
import com.wisp.game.bet.recharge.dao.entity.NodeEntity;
import com.wisp.game.bet.recharge.dao.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService extends CrudService<RoleDao, RoleEntity>
{

    @Autowired
    private YmlConfig ymlConfig;

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    public  Object List()
    {
        return null;
    }

    public List<RoleEntity> getRoleList(long adminId)
    {
        List<RoleEntity> list  = roleService.getRoleList(adminId);
        return list;
    }

    /**
     * 检查是否有此权限
     * @param adminId
     * @param permissions
     * @return
     */
    public boolean VerifyRolePermission(long adminId,String permissions)
    {
        //是否是超级账号
        if( adminId == ymlConfig.getSystemAdminId() )
        {
            return  true;
        }

        AdminEntity adminEntity = adminService.get(adminId);
        if( adminEntity == null )
        {
            return false;
        }

        String adminRole = adminEntity.getRole();

        List<Integer> adminRoleArr = StringUtils.convertStringToArray(adminRole);
        String adminRolePermission = "";
        for( int i = 0;i < adminRoleArr.size();i++ )
        {
            RoleEntity roleEntity =  roleService.get(adminRoleArr.get(i));
            if( roleEntity == null )
            {
                logger.warn("the role {} is not in role db",adminRoleArr.get(i));
                return false;
            }

            adminRolePermission += roleEntity.getPermission();
        }

        List<Integer> permissionsArr = StringUtils.convertStringToArray(permissions);
        List<Integer> adminPermissionArr = StringUtils.convertStringToArray(adminRolePermission);

        for(int i = 0; i < permissionsArr.size();i++)
        {
            if( adminPermissionArr.contains(permissionsArr.get(i)) )
            {
                continue;
            }

            if( SpecialPermission(permissionsArr.get(i)) )
            {
                continue;
            }

            return false;
        }

        return true;
    }

    private boolean SpecialPermission(int needSpecial)
    {
        if( ymlConfig.getSpecialPermissions().contains(needSpecial) )
        {
            return true;
        }

        return false;
    }
}
