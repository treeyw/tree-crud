package io.github.treeyw.crud.controller;

import io.github.treeyw.crud.config.datasource.model.QueryTypeBO;
import io.github.treeyw.crud.model.ApiResult;
import io.github.treeyw.crud.model.demo.TreeywDemoDO;
import io.github.treeyw.crud.service.common.ParentSevice;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = {"/sysCrudDemo/"})
public class SysDemoController extends ParentSevice {

    //查询参数里有值则自动按值查询，例如 name=xx，page=1 pageSize=20  sort = id 等
    @RequestMapping("list")
    public ApiResult list(TreeywDemoDO to) throws Exception {
        //补充查询
        to.addWhere("id", QueryTypeBO.GT, 0);
        return ApiResult.ok(parentQuery.listQuery(to));
    }

    //查询list，无需count
    @RequestMapping("listSelect")
    public ApiResult listSelect(TreeywDemoDO to) throws Exception {
        return ApiResult.ok(parentQuery.listQuery(to, false, true, "id,command"));
    }

    //新增或修改 支持事务，无id新增，有id修改
    @Transactional
    @RequestMapping("save")
    public ApiResult save(TreeywDemoDO to) throws Exception {

        return ApiResult.ok(parentModify.save(to));
    }

    //删除
    @RequestMapping("del")
    public ApiResult del(TreeywDemoDO to) throws Exception {
        //物理删除为parentModify.sysDeleteById(to.getId());
        return ApiResult.ok(parentModify.parentDelete(to));
    }




}
