package rs.pelotas.arch.helper;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import rs.pelotas.arch.enumeration.Method;
import rs.pelotas.arch.enumeration.OrderBy;
import rs.pelotas.arch.enumeration.Param;

/**
 *
 * @author Rafael Guterres
 */
public class QueryString {
        
    private static final String DELIMITER_PARAM_VALUE = ",";
    private static final String ORDERBY_ASC_OPERATOR = "+";
    private static final String ORDERBY_DESC_OPERATOR = "-";
    private static final String GREATER_OPERATOR = ">";
    private static final String LESS_OPERATOR = "<";
    private static final String NOT_OPERATOR = "!";
    private static final String LIKE_OPERATOR = "*";
    
    private Integer offset;
    private Integer limit;
    private List<String> fieldList;
    private List<Field> sortList;
    private List<Field> filterList;

    public QueryString() {
    }

    public QueryString(HttpServletRequest request) {
        setOffset(request.getParameter(Param.OFFSET.name().toLowerCase()));
        setLimit(request.getParameter(Param.LIMIT.name().toLowerCase()));
        setFieldList(request.getParameter(Param.FIELDS.name().toLowerCase()));
        setSortList(request.getParameter(Param.SORT.name().toLowerCase()));
        setFilterList(request);
    }

    public Integer getOffset() {
        return offset;
    }

    private void setOffset(String offset) {
        try {
            this.offset = Integer.parseInt(offset);
        } catch (NumberFormatException e) {
            this.offset = null;
        }
    }
    
    public Integer getLimit() {
        return limit;
    }

    private void setLimit(String limit) {
        try {
            this.limit = Integer.parseInt(limit);
        } catch (NumberFormatException e) {
            this.limit = null;
        }
    }
    
    public List<String> getFieldList() {
        return fieldList;
    }

    private void setFieldList(String fieldParams) {
        this.fieldList = new ArrayList<>();
        if (fieldParams != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(fieldParams, DELIMITER_PARAM_VALUE);
            while(stringTokenizer.hasMoreTokens()) {
                this.fieldList.add(stringTokenizer.nextToken());
            }
        }
    }

    public List<Field> getSortList() {
        return sortList;
    }

    private void setSortList(String sortParams) {
        this.sortList = new ArrayList<>();
        if (sortParams != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(sortParams, DELIMITER_PARAM_VALUE);
            while(stringTokenizer.hasMoreTokens()) {
                Field field = new Field();
                String fieldParam = stringTokenizer.nextToken();
                String order;
                switch (fieldParam.substring(0, 1)) {
                    case ORDERBY_DESC_OPERATOR:
                        order = OrderBy.DESC.name();
                        fieldParam = fieldParam.substring(1);
                        break;
                    case ORDERBY_ASC_OPERATOR:
                        fieldParam = fieldParam.substring(1);
                    default:
                        order = OrderBy.ASC.name();
                        break;
                }
                field.setName(fieldParam);
                field.setOrderBy(OrderBy.valueOf(order));
                this.sortList.add(field);
            }
        }
    }    
    
    public List<Field> getFilterList() {
        return filterList;
    }

    private void setFilterList(HttpServletRequest request) {
        this.filterList = new ArrayList<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            try {
                Param.valueOf(paramName.toUpperCase());
            } catch (IllegalArgumentException e) {
                Field field = new Field();
                field.setName(paramName);
                field.setValue(request.getParameter(paramName));
                this.defineFieldMethod(field);
                this.filterList.add(field);
            }
        }
    }
    
    private void defineFieldMethod(Field field) {
        this.defineFieldMethodFromFieldName(field);
        this.defineFieldMethodFromFieldValue(field);
        if (field.getMethod() == null) {
            field.setMethod(Method.EQUAL);    
        }
    }
    
    private void defineFieldMethodFromFieldName(Field field) {
        if (field.getName() != null) {
            String operatorLeft = field.getName().substring(field.getName().length() -1);
            switch(operatorLeft) {
                case GREATER_OPERATOR:
                    field.setMethod(Method.GREATER_OR_EQUAL);
                    break;
                case LESS_OPERATOR:
                    field.setMethod(Method.LESS_OR_EQUAL);
                    break;
                case NOT_OPERATOR:
                    field.setMethod(Method.NOT_EQUAL);
            }
            if (field.getMethod() != null) {
                field.setName(field.getName().substring(0, field.getName().length() -1));
            }
        }
    }
    
    private void defineFieldMethodFromFieldValue(Field field) {
        if (field.getValue() == null || field.getValue().toString().isEmpty()) {
            String[] fieldArray = null;
            if (field.getName().contains(GREATER_OPERATOR)) {
                field.setMethod(Method.GREATER);
                fieldArray = field.getName().split(GREATER_OPERATOR);
            } else if (field.getName().contains(LESS_OPERATOR)) {
                field.setMethod(Method.LESS);
                fieldArray = field.getName().split(LESS_OPERATOR);
            }
            field.setName(fieldArray[0]);
            field.setValue(fieldArray[1]);
        } else {
            String fieldValue = (String) field.getValue();
            if (fieldValue.contains(LIKE_OPERATOR)) {
                if (field.getMethod() != null &&
                    field.getMethod().equals(Method.NOT_EQUAL)) {
                    field.setMethod(Method.NOT_LIKE);
                } else {
                    field.setMethod(Method.LIKE);
                }
            }
        }
    }
}