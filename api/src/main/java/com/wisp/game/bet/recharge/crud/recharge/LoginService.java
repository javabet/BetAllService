package com.wisp.game.bet.recharge.crud.recharge;

import com.wisp.game.bet.recharge.dao.entity.AdminEntity;
import com.wisp.game.bet.recharge.dao.entity.NodeEntity;
import com.wisp.game.bet.recharge.dao.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoginService
{
    @Autowired
    private RoleService roleService;

    @Autowired
    private NodeService nodeService;

    public Map<String,Object> GetNavPermission(AdminEntity adminEntity)
    {
        //查找当前账号创建的role
        String roleStr = adminEntity.getRole();
        List<Integer> roleIds = new ArrayList<>();
        if( "".equals(roleStr) == false )
        {
            String[] roles = roleStr.split(",");
            for(int i = 0; i < roles.length;i++)
            {
                if( roles[i].equals("") )
                {
                    continue;
                }
                roleIds.add(Integer.valueOf(roles[i]));
            }
        }

        List<RoleEntity> roleEntityList = roleService.DbInIds(roleIds);
        StringBuilder roleNodeSb = new StringBuilder();
        for(int i = 0; i < roleEntityList.size();i++)
        {
            roleNodeSb.append(roleEntityList.get(i).getNode());
        }
        String roleNodeIdStr =  roleNodeSb.toString();
        String[] roleNodeIds = roleNodeIdStr.split(",");

        List<Integer> roleNodeList = new ArrayList<>();
        for(int i = 0; i < roleNodeIds.length;i++)
        {
            if(roleNodeIds[i].equals(""))
            {
                continue;
            }
            roleNodeList.add(Integer.valueOf(roleNodeIds[i]));
        }

        List<NodeEntity> nodeEntityList =  nodeService.DbInIds(roleNodeList);

        StringBuilder parentTreeSb = new StringBuilder();
        for(int i = 0; i < nodeEntityList.size();i++)
        {
            parentTreeSb.append(nodeEntityList.get(i).getParentTree());
        }
        String parentTreeStr = parentTreeSb.toString();

        List<Integer> parentTreeNodeList = new ArrayList<>();
        String[] parentTreeList = parentTreeStr.split(",");
        for(int i = 0; i < parentTreeList.length;i++)
        {
            if( parentTreeList[i].equals("") )
            {
                continue;
            }
            parentTreeNodeList.add( Integer.valueOf(parentTreeList[i]) );
        }
        List<NodeEntity> parentNodeEntityList = nodeService.DbInIds(parentTreeNodeList);


        String createRoleStr = adminEntity.getCreateRole();
        List<RoleEntity> createRoleEntityList = new ArrayList<>();
        if( createRoleStr.equals("") == false )
        {
            String[] createRole = createRoleStr.split(",");
            List<Integer> createRoleIds = new ArrayList<>();
            for(int i = 0; i < createRole.length;i++)
            {
                if( createRole[i].equals("") )
                {
                    continue;
                }
                createRoleIds.add(Integer.valueOf( createRole[i] ));
            }
            createRoleEntityList = roleService.DbInIds( createRoleIds );
        }

        Map<String,Object> map = new HashMap<>();
        map.put("Nav",parentNodeEntityList);
        map.put("CRole",createRoleEntityList);
        map.put("Role",roleEntityList);

        return map;
    }
}
