# -----------------------------------------------------------
# A Dockerfile to build a Coherence image that works out of
# the box with the Coherence Kubernetes Operator.
#
# This image uses a distroless base image as this is more
# secure than a base image that contains a full Linux O/S
# distribution.
# -----------------------------------------------------------
FROM gcr.io/distroless/java17-debian11

# Configure the image's health check command
# Health checks will only work with Coherence 22.06 and later
HEALTHCHECK  --start-period=10s --interval=30s \
    CMD ["java", \
    "-cp", "/app/libs/coherence.jar", \
    "com.tangosol.util.HealthCheckClient", \
    "http://127.0.0.1:6676/ready", \
    "||", "exit", "1"]

# Expose any default ports
# The default Coherence gRPC port
EXPOSE 1408
# The default Coherence metrics port
EXPOSE 9612

# Set the entry point to be the Java command to run
ENTRYPOINT ["java", "-cp", "/app/classes:/app/libs/*", "-Dcoherence.grpc.server.port=1408", "com.tangosol.net.Coherence"]

# Set any environment variables
# Use POF by default
ENV COHERENCE_SERIALIZER=pof
# Set the health check port to a fixed value (corresponding to the command above)
ENV COHERENCE_HEALTH_HTTP_PORT=6676
# Enable Coherence metics
ENV COHERENCE_METRICS_HTTP_ENABLED=true
# Set the Coherence log level to debug logging
ENV COHERENCE_LOG_LEVEL=9

# Copy all the application files into the /app directory in the image
# This is the default structure supported by the Coherence Operator
COPY app app


