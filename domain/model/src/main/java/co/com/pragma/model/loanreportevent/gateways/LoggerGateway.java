package co.com.pragma.model.loanreportevent.gateways;

public interface LoggerGateway {

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void error(String message, Object... args);
}
