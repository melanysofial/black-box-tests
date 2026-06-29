/**
 * Gestion de Estudiantes — CRUD completo.
 * Campos con validacion para pruebas de:
 *   - Particion de equivalencia (nombre, apellido, email, fecha)
 *   - Analisis de limites (longitud 2-50, edad minima 17)
 */
(function () {
    'use strict';

    const form = document.getElementById('estudianteForm');
    const alertBox = document.getElementById('alertBox');
    const tbody = document.getElementById('estudiantesTableBody');
    const btnSubmit = document.getElementById('btnSubmit');
    const btnCancel = document.getElementById('btnCancel');
    const formTitle = document.getElementById('formTitle');
    const grupoEstado = document.getElementById('grupoEstado');
    const idField = document.getElementById('estudianteId');

    let editingId = null;

    // --- Cargar lista ---
    function cargarEstudiantes() {
        tbody.innerHTML = '<tr><td colspan="7" class="empty-state">Cargando...</td></tr>';
        api.get('/estudiantes')
            .then(estudiantes => {
                if (estudiantes.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="7" class="empty-state">No hay estudiantes registrados</td></tr>';
                    return;
                }
                tbody.innerHTML = estudiantes.map(e => `
                    <tr>
                        <td>${e.id}</td>
                        <td>${escapeHtml(e.nombre)}</td>
                        <td>${escapeHtml(e.apellido)}</td>
                        <td>${escapeHtml(e.email)}</td>
                        <td>${e.fechaNacimiento}</td>
                        <td><span class="badge badge-${e.estado.toLowerCase()}">${e.estado}</span></td>
                        <td>
                            <div class="actions">
                                <button class="btn btn-sm" onclick="editarEstudiante(${e.id})">Editar</button>
                                <button class="btn btn-sm btn-danger" onclick="eliminarEstudiante(${e.id}, '${escapeHtml(e.nombre)}')">Eliminar</button>
                            </div>
                        </td>
                    </tr>
                `).join('');
            })
            .catch(err => {
                tbody.innerHTML = '<tr><td colspan="7" class="empty-state">Error al cargar: ' + escapeHtml(err.message) + '</td></tr>';
            });
    }

    // --- Enviar formulario ---
    form.addEventListener('submit', function (e) {
        e.preventDefault();
        limpiarErrores();
        ocultarAlerta();

        const data = {
            nombre: form.nombre.value.trim(),
            apellido: form.apellido.value.trim(),
            email: form.email.value.trim(),
            fechaNacimiento: form.fechaNacimiento.value
        };

        if (editingId) {
            data.estado = form.estado.value;
        }

        const promise = editingId
            ? api.put('/estudiantes/' + editingId, data)
            : api.post('/estudiantes', data);

        promise
            .then(() => {
                showToast(editingId ? 'Estudiante actualizado' : 'Estudiante creado', 'success');
                resetForm();
                cargarEstudiantes();
            })
            .catch(err => {
                mostrarAlerta(err.message, 'error');
            });
    });

    // --- Cancelar edicion ---
    btnCancel.addEventListener('click', resetForm);

    // --- Funciones globales para botones en la tabla ---
    window.editarEstudiante = function (id) {
        api.get('/estudiantes/' + id)
            .then(e => {
                editingId = id;
                idField.value = id;
                form.nombre.value = e.nombre;
                form.apellido.value = e.apellido;
                form.email.value = e.email;
                form.fechaNacimiento.value = e.fechaNacimiento;
                form.estado.value = e.estado;
                grupoEstado.style.display = 'block';
                formTitle.textContent = 'Editar Estudiante';
                btnSubmit.textContent = 'Actualizar';
                btnCancel.style.display = 'inline-block';
                form.scrollIntoView({ behavior: 'smooth' });
            })
            .catch(err => showToast(err.message, 'error'));
    };

    window.eliminarEstudiante = function (id, nombre) {
        if (!confirm('Eliminar a "' + nombre + '"? Esta accion no se puede deshacer.')) return;
        api.del('/estudiantes/' + id)
            .then(() => {
                showToast('Estudiante eliminado', 'success');
                if (editingId === id) resetForm();
                cargarEstudiantes();
            })
            .catch(err => showToast(err.message, 'error'));
    };

    function resetForm() {
        form.reset();
        idField.value = '';
        editingId = null;
        grupoEstado.style.display = 'none';
        formTitle.textContent = 'Nuevo Estudiante';
        btnSubmit.textContent = 'Guardar';
        btnCancel.style.display = 'none';
        limpiarErrores();
        ocultarAlerta();
    }

    // --- Helpers ---
    function mostrarAlerta(msg, type) {
        alertBox.textContent = msg;
        alertBox.className = 'alert alert-' + type + ' visible';
    }

    function ocultarAlerta() {
        alertBox.className = 'alert';
    }

    function limpiarErrores() {
        document.querySelectorAll('.error-msg').forEach(el => el.classList.remove('visible'));
        document.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
    }

    function escapeHtml(str) {
        if (!str) return '';
        return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }

    // --- Inicializar ---
    document.getElementById('year').textContent = new Date().getFullYear();
    cargarEstudiantes();
})();
