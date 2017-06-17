import ch.qos.logback.classic.encoder.PatternLayoutEncoder

stdout = "stdout"

appender(stdout, ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "[%date{yyyy-MM-dd'T'HH:mm:ss.SSS'Z'}] %-5level; %logger{0}; %msg%n"
    }
}

allAppenders = [stdout]

root(INFO, allAppenders)

customLoggers = [
        "pl.edu.agh.workflowPerformance"
]

for (customLogger in customLoggers) {
    logger(customLogger, DEBUG, allAppenders, false)
}
