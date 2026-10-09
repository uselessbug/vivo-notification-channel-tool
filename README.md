# vivo Development Mode Notification

通过 ADB / Shizuku Shell **单独关闭 vivo「正处于开发模式」通知**，不关闭开发者选项，无需 Root。

将 Release 中的 `vivo-development-mode-channel.jar` 放进手机 `Download` 目录，然后执行：

```sh
rm -f /data/local/tmp/vivo-nc.jar
cp /sdcard/Download/vivo-development-mode-channel.jar /data/local/tmp/vivo-nc.jar
chmod 644 /data/local/tmp/vivo-nc.jar
CLASSPATH=/data/local/tmp/vivo-nc.jar app_process /system/bin VivoNotificationChannelTool disable
```

将最后的 `disable` 改成 `inspect` 可查看状态（只读），改成 `restore` 可尝试恢复。

仅修改 `com.vivo.daemonService` 的 `DEVELOPMENT_MODE` 渠道（`blockable=true`、`importance=0`）。原方案已在 vivo PD2502 / Android 17 实机验证；本仓库改用 D8 构建，使用隐藏 API，其他系统版本不保证兼容。

构建：`bash scripts/test.sh && bash scripts/build.sh`（JDK 17、Android SDK API 36 / Build Tools 36.0.0、Python 3）。推送 `v*` 标签由 GitHub Actions 自动发布。

MIT License.
