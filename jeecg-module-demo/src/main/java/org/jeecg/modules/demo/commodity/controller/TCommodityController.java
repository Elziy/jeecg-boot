package org.jeecg.modules.demo.commodity.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.demo.commodity.entity.TCommodity;
import org.jeecg.modules.demo.commodity.service.ITCommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 商品管理
 * @Author: jeecg-boot
 * @Date: 2023-09-11
 * @Version: V1.0
 */
@Api(tags = "商品管理")
@RestController
@RequestMapping("/commodity/tCommodity")
@Slf4j
public class TCommodityController extends JeecgController<TCommodity, ITCommodityService> {
    @Autowired
    private ITCommodityService tCommodityService;
    
    /**
     * 分页列表查询
     *
     * @param tCommodity
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "商品管理-分页列表查询")
    @ApiOperation(value = "商品管理-分页列表查询", notes = "商品管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TCommodity>> queryPageList(TCommodity tCommodity,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        QueryWrapper<TCommodity> queryWrapper = new QueryWrapper<>();
        String priceRange = tCommodity.getPriceRange();
        if (priceRange != null && !priceRange.isEmpty()) {
            String[] split = priceRange.split(",");
            queryWrapper.ge("price", split[0]);
            queryWrapper.le("price", split[1]);
        }
        if (tCommodity.getStatus() != null && !tCommodity.getStatus().isEmpty())
            queryWrapper.eq("status", tCommodity.getStatus());
        if (tCommodity.getName() != null && !tCommodity.getName().isEmpty())
            queryWrapper.like("name", tCommodity.getName());
        Page<TCommodity> page = new Page<TCommodity>(pageNo, pageSize);
        IPage<TCommodity> pageList = tCommodityService.page(page, queryWrapper);
        return Result.OK(pageList);
    }
    
    /**
     * 添加
     *
     * @param tCommodity
     * @return
     */
    @AutoLog(value = "商品管理-添加")
    @ApiOperation(value = "商品管理-添加", notes = "商品管理-添加")
    @RequiresPermissions("commodity:t_commodities:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody TCommodity tCommodity) {
        tCommodityService.save(tCommodity);
        return Result.OK("添加成功！");
    }
    
    /**
     * 编辑
     *
     * @param tCommodity
     * @return
     */
    @AutoLog(value = "商品管理-编辑")
    @ApiOperation(value = "商品管理-编辑", notes = "商品管理-编辑")
    @RequiresPermissions("commodity:t_commodities:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody TCommodity tCommodity) {
        tCommodityService.updateById(tCommodity);
        return Result.OK("编辑成功!");
    }
    
    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商品管理-通过id删除")
    @ApiOperation(value = "商品管理-通过id删除", notes = "商品管理-通过id删除")
    @RequiresPermissions("commodity:t_commodities:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        tCommodityService.removeById(id);
        return Result.OK("删除成功!");
    }
    
    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "商品管理-批量删除")
    @ApiOperation(value = "商品管理-批量删除", notes = "商品管理-批量删除")
    @RequiresPermissions("commodity:t_commodities:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.tCommodityService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }
    
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "商品管理-通过id查询")
    @ApiOperation(value = "商品管理-通过id查询", notes = "商品管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<TCommodity> queryById(@RequestParam(name = "id", required = true) String id) {
        TCommodity tCommodity = tCommodityService.getById(id);
        if (tCommodity == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(tCommodity);
    }
    
    /**
     * 导出excel
     *
     * @param request
     * @param tCommodity
     */
    @RequiresPermissions("commodity:t_commodities:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TCommodity tCommodity) {
        return super.exportXls(request, tCommodity, TCommodity.class, "商品管理");
    }
    
    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("commodity:t_commodities:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TCommodity.class);
    }
    
}
