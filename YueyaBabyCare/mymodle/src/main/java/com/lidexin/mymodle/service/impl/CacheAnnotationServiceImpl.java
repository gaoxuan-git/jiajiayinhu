package com.lidexin.mymodle.service.impl;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.lidexin.mymodle.dto.AddCacheAnnotationRequest;
import com.lidexin.mymodle.dto.CacheMethodInfoDTO;
import com.lidexin.mymodle.exception.BusinessException;
import com.lidexin.mymodle.service.CacheAnnotationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 缓存注解管理服务实现类
 */
@Slf4j
@Service
public class CacheAnnotationServiceImpl implements CacheAnnotationService {
    
    // 项目源代码路径
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String SOURCE_PATH = PROJECT_PATH + "/src/main/java";
    // 业务包路径（只扫描业务代码）
    private static final String BUSINESS_PACKAGE_PATH = "/com/lidexin/business";
    
    @Override
    @Cacheable(value = "systemCache", key = "'scanCacheableMethods'")
    public List<CacheMethodInfoDTO> scanCacheableMethods() {
        log.info("执行缓存注解扫描...");
        List<CacheMethodInfoDTO> result = new ArrayList<>();
        
        try {
            // 只扫描 business 包下的 Service 实现类
            Path businessPath = Paths.get(SOURCE_PATH + BUSINESS_PACKAGE_PATH);
            
            // 如果 business 包不存在，返回空列表
            if (!Files.exists(businessPath)) {
                log.warn("业务包不存在: {}, 请先使用代码生成器生成业务代码", businessPath);
                return result;
            }
            
            try (Stream<Path> paths = Files.walk(businessPath)) {
                List<Path> javaFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith("ServiceImpl.java"))
                    .collect(Collectors.toList());
                
                log.info("在 business 包中找到 {} 个 ServiceImpl 文件", javaFiles.size());
                
                for (Path javaFile : javaFiles) {
                    result.addAll(scanFileForCacheableMethods(javaFile.toFile()));
                }
            }
        } catch (Exception e) {
            log.error("扫描可缓存方法失败", e);
            throw new BusinessException("扫描失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 扫描单个文件中的可缓存方法
     */
    private List<CacheMethodInfoDTO> scanFileForCacheableMethods(File file) {
        List<CacheMethodInfoDTO> methods = new ArrayList<>();
        
        try {
            JavaParser javaParser = new JavaParser();
            CompilationUnit cu = javaParser.parse(file).getResult().orElse(null);
            
            if (cu == null) {
                return methods;
            }
            
            // 获取包名
            String packageName = cu.getPackageDeclaration()
                .map(pd -> pd.getNameAsString())
                .orElse("");
            
            // 遍历所有类
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
                String className = packageName + "." + classDecl.getNameAsString();
                String simpleClassName = classDecl.getNameAsString();
                
                // 遍历所有public方法
                classDecl.getMethods().forEach(method -> {
                    if (method.isPublic() && !method.isConstructorDeclaration()) {
                        CacheMethodInfoDTO info = analyzeMethod(
                            className, 
                            simpleClassName, 
                            method, 
                            file.getAbsolutePath()
                        );
                        
                        if (info != null) {
                            methods.add(info);
                        }
                    }
                });
            });
        } catch (Exception e) {
            log.warn("解析文件失败: {}", file.getAbsolutePath(), e);
        }
        
        return methods;
    }
    
    /**
     * 分析方法并判断是否可以添加缓存
     */
    private CacheMethodInfoDTO analyzeMethod(String className, String simpleClassName, 
                                              MethodDeclaration method, String filePath) {
        String methodName = method.getNameAsString();
        String returnType = method.getTypeAsString();
        
        // 检查是否已有缓存注解
        boolean hasCacheAnnotation = method.getAnnotations().stream()
            .anyMatch(ann -> {
                String annName = ann.getNameAsString();
                return annName.equals("Cacheable") || 
                       annName.equals("CachePut") || 
                       annName.equals("CacheEvict") ||
                       annName.equals("Caching");
            });
        
        // 判断方法类型
        String methodType = determineMethodType(methodName);
        String suggestedAnnotation = determineSuggestedAnnotation(methodType);
        
        // 返回所有符合缓存条件的方法（包括已有注解的）
        if (methodType != null) {
            CacheMethodInfoDTO info = new CacheMethodInfoDTO();
            info.setClassName(className);
            info.setSimpleClassName(simpleClassName);
            info.setMethodName(methodName);
            info.setMethodSignature(method.getDeclarationAsString(false, false, false));
            info.setReturnType(returnType);
            info.setMethodType(methodType);
            info.setSuggestedAnnotation(suggestedAnnotation);
            info.setCacheName(generateCacheName(simpleClassName));
            info.setHasCacheAnnotation(hasCacheAnnotation);  // 标记是否已有注解
            info.setFilePath(filePath);
            
            return info;
        }
        
        return null;
    }
    
    /**
     * 判断方法类型
     */
    private String determineMethodType(String methodName) {
        String lowerName = methodName.toLowerCase();
        
        // 查询方法（包含 by、all、count 等后缀）
        if (lowerName.startsWith("get") || 
            lowerName.startsWith("find") || 
            lowerName.startsWith("list") || 
            lowerName.startsWith("query") || 
            lowerName.startsWith("select") ||
            lowerName.startsWith("page") ||
            lowerName.startsWith("search") ||
            lowerName.startsWith("count") ||
            lowerName.startsWith("exist")) {
            return "query";
        }
        
        // 新增方法
        if (lowerName.startsWith("add") || 
            lowerName.startsWith("insert") || 
            lowerName.startsWith("create") ||
            lowerName.startsWith("register")) {  // register 也算新增
            return "insert";
        }
        
        // 更新方法
        if (lowerName.startsWith("update") || 
            lowerName.startsWith("modify") || 
            lowerName.startsWith("save") ||
            lowerName.startsWith("edit") ||
            lowerName.startsWith("change")) {
            return "update";
        }
        
        // 删除方法
        if (lowerName.startsWith("delete") || 
            lowerName.startsWith("remove") ||
            lowerName.startsWith("clear") ||
            lowerName.startsWith("drop")) {
            return "delete";
        }
        
        return null;
    }
    
    /**
     * 根据方法类型确定建议的注解
     */
    private String determineSuggestedAnnotation(String methodType) {
        if (methodType == null) {
            return null;
        }
        
        switch (methodType) {
            case "query":
                return "@Cacheable";
            case "insert":
                return "@CacheEvict";  // 新增后清空缓存
            case "update":
                return "@CachePut";    // 更新后更新缓存
            case "delete":
                return "@CacheEvict";  // 删除后清空缓存
            default:
                return null;
        }
    }
    
    /**
     * 生成缓存名称
     */
    private String generateCacheName(String simpleClassName) {
        // 去掉 ServiceImpl 后缀，转换为小写并加上Cache后缀
        String name = simpleClassName.replace("ServiceImpl", "");
        return name.substring(0, 1).toLowerCase() + name.substring(1) + "Cache";
    }
    
    @Override
    @CacheEvict(value = "systemCache", key = "'scanCacheableMethods'")
    public Integer addCacheAnnotations(AddCacheAnnotationRequest request) {
        log.info("添加缓存注解，清除扫描缓存");
        int successCount = 0;
        
        for (AddCacheAnnotationRequest.CacheMethodItem item : request.getMethods()) {
            try {
                addCacheAnnotationToFile(item);
                successCount++;
            } catch (Exception e) {
                log.error("添加缓存注解失败: {} - {}", item.getClassName(), item.getMethodName(), e);
            }
        }
        
        return successCount;
    }
    
    /**
     * 向文件中添加缓存注解
     */
    private void addCacheAnnotationToFile(AddCacheAnnotationRequest.CacheMethodItem item) throws Exception {
        File file = new File(item.getFilePath());
        
        if (!file.exists()) {
            throw new BusinessException("文件不存在: " + item.getFilePath());
        }
        
        JavaParser javaParser = new JavaParser();
        CompilationUnit cu = javaParser.parse(file).getResult()
            .orElseThrow(() -> new BusinessException("解析文件失败"));
        
        // 确保导入了缓存注解
        ensureCacheImports(cu, item.getAnnotationType());
        
        // 找到目标方法并添加注解
        boolean[] modified = {false};
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            if (method.getNameAsString().equals(item.getMethodName())) {
                addAnnotationToMethod(method, item);
                modified[0] = true;
            }
        });
        
        if (!modified[0]) {
            throw new BusinessException("未找到方法: " + item.getMethodName());
        }
        
        // 保存修改后的文件
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(cu.toString());
        }
        
        log.info("成功添加缓存注解: {} - {}", item.getClassName(), item.getMethodName());
    }
    
    /**
     * 确保导入了缓存注解
     */
    private void ensureCacheImports(CompilationUnit cu, String annotationType) {
        String importStatement = "org.springframework.cache.annotation." + annotationType;
        
        // 检查是否已导入
        boolean hasImport = cu.getImports().stream()
            .anyMatch(imp -> imp.getNameAsString().equals(importStatement));
        
        if (!hasImport) {
            cu.addImport(importStatement);
        }
    }
    
    /**
     * 向方法添加注解
     */
    private void addAnnotationToMethod(MethodDeclaration method, AddCacheAnnotationRequest.CacheMethodItem item) {
        // 创建注解
        NormalAnnotationExpr annotation = new NormalAnnotationExpr();
        annotation.setName(item.getAnnotationType());
        
        NodeList<MemberValuePair> pairs = new NodeList<>();
        
        // 添加 value 参数（缓存名称）
        pairs.add(new MemberValuePair("value", new StringLiteralExpr(item.getCacheName())));
        
        // 添加 key 参数
        if (item.getCacheKey() != null && !item.getCacheKey().isEmpty()) {
            // 用户自定义 key
            pairs.add(new MemberValuePair("key", new StringLiteralExpr(item.getCacheKey())));
        } else {
            // 自动生成 key
            String generatedKey = generateCacheKey(method, item.getAnnotationType());
            if (generatedKey != null) {
                pairs.add(new MemberValuePair("key", new StringLiteralExpr(generatedKey)));
            }
        }
        
        // 对于 CacheEvict，添加 allEntries 参数（如果方法没有参数）
        if ("CacheEvict".equals(item.getAnnotationType()) && method.getParameters().isEmpty()) {
            pairs.add(new MemberValuePair("allEntries", new StringLiteralExpr("true")));
        }
        
        annotation.setPairs(pairs);
        method.addAnnotation(annotation);
    }
    
    /**
     * 智能生成缓存 key 表达式
     */
    private String generateCacheKey(MethodDeclaration method, String annotationType) {
        int paramCount = method.getParameters().size();
        
        if (paramCount == 0) {
            // 无参数：使用固定 key 或不指定（对于查询）；CacheEvict 使用 allEntries
            if ("CacheEvict".equals(annotationType)) {
                return null; // CacheEvict 无参数时使用 allEntries
            }
            return "'all'"; // 查询方法无参数时使用固定 key
        } else if (paramCount == 1) {
            // 单参数：直接使用参数
            String paramName = method.getParameter(0).getNameAsString();
            String paramType = method.getParameter(0).getTypeAsString();
            
            // 如果是对象类型，尝试使用 id 属性
            if (!isPrimitiveOrWrapper(paramType) && !paramType.equals("String")) {
                return "#" + paramName + ".id";
            }
            return "#" + paramName;
        } else {
            // 多参数：组合所有参数
            List<String> keyParts = new ArrayList<>();
            
            for (int i = 0; i < paramCount; i++) {
                String paramName = method.getParameter(i).getNameAsString();
                String paramType = method.getParameter(i).getTypeAsString();
                
                // 处理不同类型的参数
                if (isPrimitiveOrWrapper(paramType) || paramType.equals("String")) {
                    // 基本类型或 String：直接使用
                    keyParts.add("#" + paramName);
                } else if (paramType.contains("Request") || paramType.contains("Query") || paramType.contains("Page")) {
                    // 分页/查询对象：尝试提取常见字段
                    // 对于 PageRequest，使用 current 和 size
                    keyParts.add("#" + paramName + ".current");
                    keyParts.add("#" + paramName + ".size");
                } else {
                    // 普通对象：使用 id 属性
                    keyParts.add("#" + paramName + ".id");
                }
            }
            
            // 构建完整的 SpEL 表达式
            if (keyParts.isEmpty()) {
                return "'default'";
            }
            
            // 使用 StringBuilder 拼接
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < keyParts.size(); i++) {
                if (i > 0) {
                    result.append(" + '-' + ");
                }
                result.append(keyParts.get(i));
            }
            
            return result.toString();
        }
    }
    
    /**
     * 判断是否为基本类型或包装类
     */
    private boolean isPrimitiveOrWrapper(String type) {
        return type.equals("int") || type.equals("Integer") ||
               type.equals("long") || type.equals("Long") ||
               type.equals("boolean") || type.equals("Boolean") ||
               type.equals("double") || type.equals("Double") ||
               type.equals("float") || type.equals("Float") ||
               type.equals("short") || type.equals("Short") ||
               type.equals("byte") || type.equals("Byte") ||
               type.equals("char") || type.equals("Character");
    }
}

