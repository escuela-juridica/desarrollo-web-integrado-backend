package pe.edu.utp.escuela.app.adminusuarios;

/** CAMBIO_PENDIENTE: falta reemplazar la contraseña temporal. PENDIENTE_VERIFICACION: falta
 * verificar el correo. AMBAS_PENDIENTES: recién creada, faltan las dos. NINGUNA: cuenta operativa. */
public enum CondicionCuentaAdmin {
    NINGUNA,
    PENDIENTE_VERIFICACION,
    CAMBIO_PENDIENTE,
    AMBAS_PENDIENTES,
}
