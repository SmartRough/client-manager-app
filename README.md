# 🔧 JavaFX EXE Builder (jlink + Caxa)

Este script `build-installer.bat` automatiza la creación de un ejecutable `.exe` standalone para tu aplicación JavaFX. Utiliza `jlink` para generar un runtime personalizado y `caxa` para empaquetar todo en un único ejecutable que **no requiere Java instalado** en la máquina del usuario.

---

## 📦 ¿Qué hace este script?

1. **Limpia carpetas anteriores** (`tmp-runtime`, `tmp-dist`, `installer`)
2. **Crea un runtime optimizado** con `jlink` usando solo los módulos necesarios
3. **Copia el JAR principal** y las librerías nativas (DLLs)
4. **Empaqueta todo** en un `.exe` con [`caxa`](https://github.com/leafac/caxa)
5. **Genera un instalador standalone** en la carpeta `installer/`

---

## 🧩 Requisitos previos

- [Java 21 JDK](https://jdk.java.net/21/)
- [JavaFX SDK 21](https://gluonhq.com/products/javafx/)
- [Node.js y npm](https://nodejs.org/)
- Caxa instalado globalmente:
  ```bash
  npm install -g caxa