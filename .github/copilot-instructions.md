# Instrucciones del proyecto

- Mantén Angular en `frontend/` y Spring Boot en `backend/`.
- Las integraciones externas de ubicación y accidentes deben seguir siendo interfaces hasta que se proporcionen sus API reales. No simules respuestas exitosas.
- Valida en frontend y backend el límite de 150 caracteres y hasta tres imágenes para reportes de unidad sucia.
- Usa variables de entorno para las credenciales de MySQL; nunca las agregues a archivos versionados.
- No expongas datos personales en URLs o logs.
- Actualiza `README.md` cuando cambien los comandos, perfiles o integraciones.

## Estado del scaffold

- [x] Requisitos Angular + Spring Boot definidos.
- [x] Proyecto creado en `TransporteEmpresarial/`.
- [x] Flujos de usuario y contratos de integración implementados.
- [x] Perfil local H2 y perfil MySQL configurables.
- [x] Documentación de ejecución creada.
- [ ] Build pendiente: este entorno no tiene Node/npm, Maven ni JDK 17+ disponibles.
- [ ] Aplicación no iniciada; instalar los prerrequisitos indicados en README primero.
