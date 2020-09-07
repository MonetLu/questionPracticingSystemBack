package com.itheima.web.controller.store;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.store.Catalog;
import com.itheima.domain.store.Company;
import com.itheima.domain.store.Question;
import com.itheima.utils.BeanUtil;
import com.itheima.web.controller.BaseServlet;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

// uri:/store/question?operation=list
@WebServlet("/store/question")
public class QuestionServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operation = request.getParameter("operation");
        if("list".equals(operation)){
            this.list(request,response);
        }else if("toAdd".equals(operation)){
            this.toAdd(request,response);
        }else if("save".equals(operation)){
            try {
                this.save(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if("toEdit".equals(operation)){
            this.toEdit(request,response);
        }else if("edit".equals(operation)){
            try {
                this.edit(request,response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if("delete".equals(operation)){
            this.delete(request,response);
        }else if("toTestUpload".equals(operation)){
            this.toTestUpload(request,response);
        }else if("testUpload".equals(operation)){
            try {
                this.testUpload(request,response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if("downloadReport".equals(operation)){
            this.downloadReport(request,response);
        }
    }

    private void toTestUpload(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/store/question/testFileUpload.jsp").forward(request,response);
    }

    private void testUpload(HttpServletRequest request,HttpServletResponse response) throws Exception {
        //1.确认该操作是否支持文件上传操作，enctype="multipart/form-data"
        if(ServletFileUpload.isMultipartContent(request)){
            //2.创建磁盘工厂对象
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //3.Servlet文件上传核心对象
            ServletFileUpload fileUpload = new ServletFileUpload(factory);
            //4.从request中读取数据
            List<FileItem> fileItems = fileUpload.parseRequest(request);

            for(FileItem item : fileItems){
                //5.当前表单是否是文件表单
                if(!item.isFormField()){
                    //6.从临时存储文件的地方将内容写入到指定位置
                    item.write(new File(this.getServletContext().getRealPath("upload"),item.getName()));
                }
            }
        }

    }

    private void list(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        //进入列表页
        //获取数据
        int page = 1;
        int size = 5;
        if(StringUtils.isNotBlank(request.getParameter("page"))){
            page = Integer.parseInt(request.getParameter("page"));
        }
        if(StringUtils.isNotBlank(request.getParameter("size"))){
            size = Integer.parseInt(request.getParameter("size"));
        }
        PageInfo all = questionService.findAll(page, size);
        //将数据保存到指定的位置
        request.setAttribute("page",all);
        //跳转页面
        request.getRequestDispatcher("/WEB-INF/pages/store/question/list.jsp").forward(request,response);
    }

    private void toAdd(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        List<Company> companyList = companyService.findAll();
        List<Catalog> catalogList = catalogService.findAll();
        request.setAttribute("companyList",companyList);
        request.setAttribute("catalogList",catalogList);

        //跳转页面
        request.getRequestDispatcher("/WEB-INF/pages/store/question/add.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request,HttpServletResponse response) throws Exception {
        //1.确认该操作是否支持文件上传操作，enctype="multipart/form-data"
        if(ServletFileUpload.isMultipartContent(request)){
            //2.创建磁盘工厂对象
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //3.Servlet文件上传核心对象
            ServletFileUpload fileUpload = new ServletFileUpload(factory);
            //4.从request中读取数据
            List<FileItem> fileItems = fileUpload.parseRequest(request);

            //创建一个标记位,标记当前时候有上传文件的操作
            boolean flag = false;
            for(FileItem item :fileItems){
                if(StringUtils.isNotBlank(item.getName())){
                    flag = true;
                    break;
                }
            }

            // --处理form表单提交过来的普通数据
            //将数据获取到，封装成一个对象
            Question question = BeanUtil.fillBean(fileItems,Question.class);
            //调用业务层接口save，返回的是图片的id，这个id就是我们存储文件时的文件名字
            String picture = questionService.save(question , flag);

            // --处理form表单提交过来的文件数据
            for(FileItem item : fileItems){
                //5.当前表单是否是文件表单
                if(!item.isFormField()){
                    //6.从临时存储文件的地方将内容写入到指定位置
                    item.write(new File(this.getServletContext().getRealPath("upload"),picture));
                }
            }
        }
        //跳转回到页面list
        response.sendRedirect(request.getContextPath()+"/store/question?operation=list");
    }

    private void toEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //查询要修改的数据findById
        String id = request.getParameter("id");
        Question question = questionService.findById(id);
        //将数据加载到指定区域，供页面获取
        request.setAttribute("question",question);

        List<Company> companyList = companyService.findAll();
        List<Catalog> catalogList = catalogService.findAll();
        request.setAttribute("companyList",companyList);
        request.setAttribute("catalogList",catalogList);

        //跳转页面
        request.getRequestDispatcher("/WEB-INF/pages/store/question/update.jsp").forward(request,response);
    }

    private void edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //1.确认该操作是否支持文件上传操作，enctype="multipart/form-data"
        if(ServletFileUpload.isMultipartContent(request)){
            //2.创建磁盘工厂对象
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //3.Servlet文件上传核心对象
            ServletFileUpload fileUpload = new ServletFileUpload(factory);
            //4.从request中读取数据，这里面是该文件此次上传的信息
            List<FileItem> fileItems = fileUpload.parseRequest(request);

            //创建一个标记位,标记当前时候有上传文件的操作
            //fileItems 是表单的每一项，由于是加了enctype="multipart/form-data"，所以只有带文件的name是非空的，其余的item的name属性都是空的
            boolean flag = false;
            for(FileItem item :fileItems){
                if(StringUtils.isNotBlank(item.getName())){
                    flag = true;
                    break;
                }
            }

            // --处理form表单提交过来的普通数据
            //将数据获取到，封装成一个对象，我们制作了一个工具类叫做BeanUnil
            Question question = BeanUtil.fillBean(fileItems,Question.class);
            //调用业务层接口save
            questionService.update(question , flag);

            // --处理form表单提交过来的文件数据
            for(FileItem item : fileItems){
                //5.当前表单是否是文件表单，如果是文件字段，执行
                //item.ifFOrmField():是文件字段，返回false；不是文件字段，返回true
                if(!item.isFormField()){
                    //6.从临时存储文件的地方将内容写入到指定位置，File的构造器是(文件路径，文件名)
                    //虚拟目录的根目录是webapp，所以upload代表webapp下的upload文件夹
                    item.write(new File(this.getServletContext().getRealPath("upload"),question.getId()));

                }
            }
        }




        //跳转回到页面list
        response.sendRedirect(request.getContextPath()+"/store/question?operation=list");
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //将数据获取到，封装成一个对象
        Question question = BeanUtil.fillBean(request,Question.class);
        //调用业务层接口save
        questionService.delete(question);
        //跳转回到页面list
        response.sendRedirect(request.getContextPath()+"/store/question?operation=list");
    }

    private void downloadReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //返回的数据类型为文件xlsx类型
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = new String("测试文件名.xlsx".getBytes(),"iso8859-1");
        response.addHeader("Content-Disposition","attachment;fileName="+fileName);

        //生成报告的文件，然后传递到前端页面
        ByteArrayOutputStream os = questionService.getReport();
        //获取产生响应的流对象
        ServletOutputStream sos = response.getOutputStream();
        //将数据从原始的字节流对象中提取出来写入到servlet对应的输出流中
        os.writeTo(sos);
        //将输出流刷新
        sos.flush();
        os.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }
}