FROM tomcat:9.0-jre8-alpine

# Limpiar las aplicaciones de prueba por defecto
RUN rm -rf /usr/local/tomcat/webapps/*

# Copiar nuestro ROOT.war para que la app responda directamente en la raiz /
COPY ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Script de inicio para adaptar el puerto dinamico de Render/Railway ($PORT)
RUN echo '#!/bin/sh' > /run.sh && \
    echo 'PORT="${PORT:-8080}"' >> /run.sh && \
    echo 'sed -i "s/port=\"8080\"/port=\"$PORT\"/g" /usr/local/tomcat/conf/server.xml' >> /run.sh && \
    echo 'exec catalina.sh run' >> /run.sh && \
    chmod +x /run.sh

ENV USE_SUPABASE=true
EXPOSE 8080

CMD ["/run.sh"]
