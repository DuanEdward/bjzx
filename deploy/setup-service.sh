#!/bin/bash

# 安装系统服务脚本

set -e

# 加载配置文件
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/config.sh"

echo "=========================================="
echo "安装系统服务..."
echo "=========================================="

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then 
    echo "请使用root用户运行此脚本"
    exit 1
fi

# 生成systemd服务文件
cat > /etc/systemd/system/bjzxjj-backend.service <<EOF
[Unit]
Description=北京中兴建机职业技能鉴定中心网站后端服务
After=network.target mysql.service

[Service]
Type=simple
User=root
WorkingDirectory=${BACKEND_DIR}
ExecStart=/usr/bin/java -Xms${JVM_XMS} -Xmx${JVM_XMX} -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -jar ${BACKEND_DIR}/bjzxjj-website.jar --spring.profiles.active=prod
ExecStop=/bin/kill -15 \$MAINPID
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=bjzxjj-backend

# 环境变量
Environment="JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk"

[Install]
WantedBy=multi-user.target
EOF

# 重载systemd配置
systemctl daemon-reload

# 启用服务（开机自启）
systemctl enable bjzxjj-backend

echo "系统服务安装完成！"
echo ""
echo "服务管理命令："
echo "  启动服务: systemctl start bjzxjj-backend"
echo "  停止服务: systemctl stop bjzxjj-backend"
echo "  重启服务: systemctl restart bjzxjj-backend"
echo "  查看状态: systemctl status bjzxjj-backend"
echo "  查看日志: journalctl -u bjzxjj-backend -f"
echo ""

