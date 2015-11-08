package arch.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import arch.enumeration.Method;
import arch.enumeration.OrderBy;
import arch.enumeration.Param;
import arch.helper.Field;

/**
 *
 * @author Rafael Guterres
 */
public class QueryString implements Serializable {

    private static final long serialVersionUID = -756050251522896230L;

    private static final String DELIMITER_PARAM_VALUE = ",";
    private static final String ORDERBY_ASC_OPERATOR = "+";
    private static final String ORDERBY_DESC_OPERATOR = "-";
    private static final String GREATER_OPERATOR = ">";
    private static final String LESS_OPERATOR = "<";
    private static final String NOT_OPERATOR = "!";
    private static final String LIKE_OPERATOR = "*";
    private static final String BETWEEN_OPERATOR = "...";
    private static final String BETWEEN_REGEX = "\\.\\.\\.";

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
            while (stringTokenizer.hasMoreTokens()) {
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
            while (stringTokenizer.hasMoreTokens()) {
                Field field = new Field();
                String fieldParam = stringTokenizer.nextToken();
                String order = null;
                switch (fieldParam.substring(0, 1)) {
                    case ORDERBY_DESC_OPERATOR:
                        order = OrderBy.DESC.name();
                        fieldParam = fieldParam.substring(1);
                        break;
                    case ORDERBY_ASC_OPERATOR:
                        fieldParam = fieldParam.substring(1);
                        order = OrderBy.ASC.name();
                        break;
                    default:
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
                Logger.getAnonymousLogger().warning(e.getMessage());
                String paramValue = request.getParameter(paramName);
                Field field = new Field();
                field.setName(paramName);
                field.setValue(paramValue);
                this.defineFieldMethod(field);
                this.filterList.add(field);
            }
        }
    }

    private void defineFieldMethod(Field field) {
        this.defineFieldMethodWithEqualsBased(field);
        this.defineFieldMethodWithComparableBased(field);
        this.defineFieldMethodWithLikeBased(field);
        this.defineFieldMethodDefault(field);
    }

    private void defineFieldMethodWithEqualsBased(Field field) {
        if (field.getName() != null) {
            String paramName = field.getName();
            String operatorLeft = paramName.substring(paramName.length() - 1);
            switch (operatorLeft) {
                case GREATER_OPERATOR:
                    field.setMethod(Method.GREATER_OR_EQUAL);
                    break;
                case LESS_OPERATOR:
                    field.setMethod(Method.LESS_OR_EQUAL);
                    break;
                case NOT_OPERATOR:
                    field.setMethod(Method.NOT_EQUAL);
                    break;
                default:
                    break;
            }
            if (field.getMethod() != null) {
                field.setName(paramName.substring(0, paramName.length() - 1));
            }
        }
    }

    private void defineFieldMethodWithComparableBased(Field field) {
        if (field.getValue() == null || field.getValue().toString().isEmpty()) {
            String[] fieldArray = null;
            if (field.getName().contains(GREATER_OPERATOR)) {
                field.setMethod(Method.GREATER);
                fieldArray = field.getName().split(GREATER_OPERATOR);
            } else if (field.getName().contains(LESS_OPERATOR)) {
                field.setMethod(Method.LESS);
                fieldArray = field.getName().split(LESS_OPERATOR);
            }
            if (fieldArray != null && fieldArray.length > 0) {
                field.setName(fieldArray[0]);
                field.setValue(fieldArray[1]);
            }
        } else if (field.getValue().toString().contains(BETWEEN_OPERATOR)) {
            String[] fieldArray = null;
            field.setMethod(Method.BETWEEN);
            fieldArray = field.getValue().toString().split(BETWEEN_REGEX);
            field.setValue(fieldArray[0]);
            field.setField(new Field(field.getName(), fieldArray[1]));
        }
    }

    private void defineFieldMethodWithLikeBased(Field field) {
        if (field.getValue() != null && field.getValue().toString().contains(LIKE_OPERATOR)) {
            if (field.getMethod() != null && field.getMethod().equals(Method.NOT_EQUAL)) {
                field.setMethod(Method.NOT_LIKE);
            } else {
                field.setMethod(Method.LIKE);
            }
        }
    }

    private void defineFieldMethodDefault(Field field) {
        if (field.getMethod() == null) {
            field.setMethod(Method.EQUAL);
        }
    }
}
