package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据
     * @param employee
     */
    void insert(Employee employee);

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

//    @Update("update employee set status = #{status} where id = #{id}")
//    void startOrStop(Integer status, Long id);

    /**
     * 根据主键来动态修改属性
     * @param employee
     */
    void update(Employee employee);

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    Employee getById(Long id);
}
