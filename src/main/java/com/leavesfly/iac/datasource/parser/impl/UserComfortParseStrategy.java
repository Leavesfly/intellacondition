package com.leavesfly.iac.datasource.parser.impl;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.datasource.parser.DataParseStrategy;
import com.leavesfly.iac.datasource.parser.ParseException;
import com.leavesfly.iac.execute.domain.ContiUserComfortFunc;
import com.leavesfly.iac.execute.domain.UserComfortFunc;
import com.leavesfly.iac.execute.domain.UserTempRange;

/**
 * 用户舒适度函数解析策略实现类
 * <p>
 * 该类负责解析用户舒适度数据格式的字符串，数据格式为：
 * 用户ID\t期望温度
 * <p>
 * 示例：0\t23
 */
public class UserComfortParseStrategy implements DataParseStrategy<UserComfortFunc> {

    /**
     * 表格分隔符（制表符）
     */
    private static final Pattern SPLITTER_TABLE = Pattern.compile("\t");

    /**
     * 数据格式验证正则表达式
     */
    private static final Pattern DATA_FORMAT_PATTERN = Pattern.compile(
            "^[^\\t]+\\t[0-9]+\\.?[0-9]*$"
    );

    @Override
    public UserComfortFunc parse(String dataLine) throws ParseException {
        if (StringUtils.isBlank(dataLine)) {
            throw new ParseException("用户舒适度数据行不能为空", dataLine, -1, getStrategyType());
        }

        String trimmedLine = dataLine.trim();

        if (!validate(trimmedLine)) {
            throw new ParseException("用户舒适度数据格式不正确", trimmedLine, -1, getStrategyType());
        }

        try {
            String[] segments = SPLITTER_TABLE.split(trimmedLine);

            // 解析用户ID
            String userId = segments[0].trim();
            if (userId.isEmpty()) {
                throw new ParseException("用户ID不能为空", trimmedLine, -1, getStrategyType());
            }

            // 解析期望温度
            float wantTemp = parseTemperature(segments[1], trimmedLine);

            // 创建温度范围对象
            UserTempRange userTempRange = new UserTempRange(
                    wantTemp - AppContextConstant.COMFORT_UP_DOWN_RANGE_VALUE,
                    wantTemp + AppContextConstant.COMFORT_UP_DOWN_RANGE_VALUE);

            return new ContiUserComfortFunc(userId, userTempRange);

        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            throw new ParseException("解析用户舒适度数据时发生未知错误: " + e.getMessage(),
                    e, trimmedLine, -1, getStrategyType());
        }
    }

    /**
     * 解析温度值
     *
     * @param tempStr  温度字符串
     * @param dataLine 数据行
     * @return 解析后的温度值
     * @throws ParseException 解析异常
     */
    private float parseTemperature(String tempStr, String dataLine) throws ParseException {
        if (!NumberUtils.isNumber(tempStr)) {
            throw new ParseException("期望温度值格式不正确: " + tempStr,
                    dataLine, -1, getStrategyType());
        }

        float temperature = NumberUtils.toFloat(tempStr);

        // 验证温度范围的合理性
        if (temperature < 0 || temperature > 50) {
            throw new ParseException("期望温度值超出合理范围(0-50°C): " + temperature,
                    dataLine, -1, getStrategyType());
        }

        return temperature;
    }

    @Override
    public boolean validate(String dataLine) {
        if (StringUtils.isBlank(dataLine)) {
            return false;
        }

        String trimmedLine = dataLine.trim();

        // 检查基本格式
        if (!DATA_FORMAT_PATTERN.matcher(trimmedLine).matches()) {
            return false;
        }

        // 检查字段数量
        String[] segments = SPLITTER_TABLE.split(trimmedLine);
        return segments.length == 2;
    }

    @Override
    public String getFormatDescription() {
        return "用户舒适度数据格式: 用户ID\\t期望温度";
    }

    @Override
    public String getStrategyType() {
        return "USER_COMFORT";
    }
}
