package com.ruoyi.project.system.user.controller;

import java.util.List;

import com.ruoyi.project.register.area.domain.Areainfo;
import com.ruoyi.project.register.area.service.IAreainfoService;
import com.ruoyi.project.register.hospital.domain.Hospitalinfo;
import com.ruoyi.project.register.hospital.service.HospitalinfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.security.ShiroUtils;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.system.post.service.IPostService;
import com.ruoyi.project.system.role.service.IRoleService;
import com.ruoyi.project.system.user.domain.User;
import com.ruoyi.project.system.user.domain.UserRole;
import com.ruoyi.project.system.user.service.IUserService;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/user")
public class UserController extends BaseController
{
    private String prefix = "system/user";

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IPostService postService;

    @Autowired
    private IAreainfoService areainfoService;


    @Autowired
    private HospitalinfoService hospitalinfoService;


    @RequiresPermissions("system:user:view")
    @GetMapping()
    public String user()
    {
        return prefix + "/user";
    }

    @RequiresPermissions("system:user:view")
    @GetMapping("/admin")
    public String admin()
    {
        return prefix + "/admin";
    }

    @RequiresPermissions("system:user:view")
    @GetMapping("/doctor")
    public String doctor()
    {
        return prefix + "/doctor";
    }

    @RequiresPermissions("system:user:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(User user)
    {
        startPage();
        user.setUserType("01");
        List<User> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @RequiresPermissions("system:user:list")
    @PostMapping("/doctorList")
    @ResponseBody
    public TableDataInfo doctorList(User user)
    {
        startPage();
        user.setUserType("02");
        List<User> list = userService.selectUserList(user);
        return getDataTable(list);
    }
    @RequiresPermissions("system:user:list")
    @PostMapping("/adminList")
    @ResponseBody
    public TableDataInfo adminList(User user)
    {
        startPage();
        user.setUserType("03");
        List<User> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "医生账户", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:docker:dockerExport")
    @PostMapping("/dockerExport")
    @ResponseBody
    public AjaxResult dockerExport(User user)
    {
        user.setUserType("02");
        List<User> list = userService.selectUserList(user);
        ExcelUtil<User> util = new ExcelUtil<User>(User.class);
        return util.exportExcel(list, "医生账户");
    }
    @Log(title = "管理员账户", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:user:adminExport")
    @PostMapping("/adminExport")
    @ResponseBody
    public AjaxResult adminExport(User user)
    {
        user.setUserType("03");
        List<User> list = userService.selectUserList(user);
        ExcelUtil<User> util = new ExcelUtil<User>(User.class);
        return util.exportExcel(list, "管理员账户");
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:user:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(User user)
    {

        user.setUserType("01");
        List<User> list = userService.selectUserList(user);
        ExcelUtil<User> util = new ExcelUtil<User>(User.class);
        return util.exportExcel(list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @RequiresPermissions("system:user:import")
    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<User> util = new ExcelUtil<User>(User.class);
        List<User> userList = util.importExcel(file.getInputStream());
        String message = userService.importUser(userList, updateSupport);
        return AjaxResult.success(message);
    }

    @RequiresPermissions("system:user:view")
    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate()
    {
        ExcelUtil<User> util = new ExcelUtil<User>(User.class);
        return util.importTemplateExcel("用户数据");
    }

    /**
     * 新增用户
     */
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        mmap.put("roles", roleService.selectRoleAll());
        mmap.put("posts", postService.selectPostAll());
        return prefix + "/add";
    }

    @GetMapping("/addDoctor")
    public String addDoctor(ModelMap mmap)
    {
        mmap.put("roles", roleService.selectRoleAll());
        mmap.put("posts", postService.selectPostAll());
        mmap.put("areainfosList", areainfoService.selectAll());
        mmap.put("HospitalinfoList", hospitalinfoService.selectAll2(new Hospitalinfo()));
        return prefix + "/addDoctor";
    }
    @GetMapping("/addAdmin")
    public String addAdmin(ModelMap mmap)
    {
        mmap.put("roles", roleService.selectRoleAll());
        mmap.put("posts", postService.selectPostAll());
        mmap.put("areainfosList", areainfoService.selectAll());
        mmap.put("HospitalinfoList", hospitalinfoService.selectAll2(new Hospitalinfo()));
        return prefix + "/addAdmin";
    }

    /**
     * 新增保存用户
     */
    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@Validated User user)
    {
        if (UserConstants.USER_NAME_NOT_UNIQUE.equals(userService.checkLoginNameUnique(user.getLoginName())))
        {
            return error("新增用户'" + user.getLoginName() + "'失败，登录账号已存在");
        }
        else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return error("新增用户'" + user.getLoginName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return error("新增用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        return toAjax(userService.insertUser(user));
    }

    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping("/addDoctorSave")
    @ResponseBody
    public AjaxResult addDoctorSave(@Validated User user)
    {
        if (UserConstants.USER_NAME_NOT_UNIQUE.equals(userService.checkLoginNameUnique(user.getLoginName())))
        {
            return error("新增用户'" + user.getLoginName() + "'失败，登录账号已存在");
        }
        else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return error("新增用户'" + user.getLoginName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return error("新增用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        user.setUserType("02");
        return toAjax(userService.insertUser(user));
    }
    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping("/addAdminSave")
    @ResponseBody
    public AjaxResult addAdminSave(@Validated User user)
    {
        if (UserConstants.USER_NAME_NOT_UNIQUE.equals(userService.checkLoginNameUnique(user.getLoginName())))
        {
            return error("新增用户'" + user.getLoginName() + "'失败，登录账号已存在");
        }
        else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return error("新增用户'" + user.getLoginName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return error("新增用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }

        user.setUserType("03");
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @GetMapping("/edit/{userId}")
    public String edit(@PathVariable("userId") Long userId, ModelMap mmap)
    {
        mmap.put("user", userService.selectUserById(userId));
        mmap.put("roles", roleService.selectRolesByUserId(userId));
        mmap.put("posts", postService.selectPostsByUserId(userId));
        mmap.put("areainfosList", areainfoService.selectAll());
        mmap.put("HospitalinfoList", hospitalinfoService.selectAll2(new Hospitalinfo()));
        return prefix + "/edit";
    }

    /**
     * 修改用户
     */
    @GetMapping("/editDoctor/{userId}")
    public String editDoctor(@PathVariable("userId") Long userId, ModelMap mmap)
    {
        mmap.put("user", userService.selectUserById(userId));
        mmap.put("roles", roleService.selectRolesByUserId(userId));
        mmap.put("posts", postService.selectPostsByUserId(userId));
        mmap.put("areainfosList", areainfoService.selectAll());
        mmap.put("HospitalinfoList", hospitalinfoService.selectAll2(new Hospitalinfo()));
        return prefix + "/editDoctor";
    }
    @GetMapping("/editAdmin/{userId}")
    public String editAdmin(@PathVariable("userId") Long userId, ModelMap mmap)
    {
        mmap.put("user", userService.selectUserById(userId));
        mmap.put("roles", roleService.selectRolesByUserId(userId));
        mmap.put("posts", postService.selectPostsByUserId(userId));
        mmap.put("areainfosList", areainfoService.selectAll());
        mmap.put("HospitalinfoList", hospitalinfoService.selectAll2(new Hospitalinfo()));
        return prefix + "/editAdmin";
    }

    /**
     * 修改保存用户
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@Validated User user)
    {
        userService.checkUserAllowed(user);
        if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return error("修改用户'" + user.getLoginName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return error("修改用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        return toAjax(userService.updateUser(user));
    }

    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/editDoctor")
    @ResponseBody
    public AjaxResult editDoctorSave(@Validated User user)
    {
        userService.checkUserAllowed(user);
        if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return error("修改用户'" + user.getLoginName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return error("修改用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        return toAjax(userService.updateUser(user));
    }
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/editAdmin")
    @ResponseBody
    public AjaxResult editAdminSave(@Validated User user)
    {
        userService.checkUserAllowed(user);
        if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return error("修改用户'" + user.getLoginName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return error("修改用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        return toAjax(userService.updateUser(user));
    }


    @RequiresPermissions("system:user:resetPwd")
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @GetMapping("/resetPwd/{userId}")
    public String resetPwd(@PathVariable("userId") Long userId, ModelMap mmap)
    {
        mmap.put("user", userService.selectUserById(userId));
        return prefix + "/resetPwd";
    }

    @RequiresPermissions("system:user:resetPwd")
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    @ResponseBody
    public AjaxResult resetPwdSave(User user)
    {
        userService.checkUserAllowed(user);
        if (userService.resetUserPwd(user) > 0)
        {
            if (ShiroUtils.getUserId() == user.getUserId())
            {
                setSysUser(userService.selectUserById(user.getUserId()));
            }
            return success();
        }
        return error();
    }

    /**
     * 进入授权角色页
     */
    @GetMapping("/authRole/{userId}")
    public String authRole(@PathVariable("userId") Long userId, ModelMap mmap)
    {
        User user = userService.selectUserById(userId);
        // 获取用户所属的角色列表
        List<UserRole> userRoles = userService.selectUserRoleByUserId(userId);
        mmap.put("user", user);
        mmap.put("userRoles", userRoles);
        return prefix + "/authRole";
    }

    /**
     * 用户授权角色
     */
    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PostMapping("/authRole/insertAuthRole")
    @ResponseBody
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds)
    {
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    @RequiresPermissions("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        try
        {
            return toAjax(userService.deleteUserByIds(ids));
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 校验用户名
     */
    @PostMapping("/checkLoginNameUnique")
    @ResponseBody
    public String checkLoginNameUnique(User user)
    {
        return userService.checkLoginNameUnique(user.getLoginName());
    }

    /**
     * 校验手机号码
     */
    @PostMapping("/checkPhoneUnique")
    @ResponseBody
    public String checkPhoneUnique(User user)
    {
        return userService.checkPhoneUnique(user);
    }

    /**
     * 校验email邮箱
     */
    @PostMapping("/checkEmailUnique")
    @ResponseBody
    public String checkEmailUnique(User user)
    {
        return userService.checkEmailUnique(user);
    }

    /**
     * 用户状态修改
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("system:user:edit")
    @PostMapping("/changeStatus")
    @ResponseBody
    public AjaxResult changeStatus(User user)
    {
        userService.checkUserAllowed(user);
        return toAjax(userService.changeStatus(user));
    }
}
