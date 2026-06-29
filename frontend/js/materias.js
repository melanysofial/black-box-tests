/**
 * Gestion de Materias — CRUD completo.
 * Campos con validacion para pruebas de:
 *   - Particion de equivalencia (codigo MAT-XXX, creditos 1-6, cupo 5-50)
 *   - Analisis de limites (creditos 0,1,6,7; cupo 4,5,50,51)
 */
(function () {
    'use strict';

    const form = document.getElementById('materiaForm');
    const alertBox = document.getElementById('alertBox');
    const tbody = document.getElementById('materiasTableBody');
    const btnSubmit = document.getElementById('btnSubmit');
    const btnCancel = document.getElementById('btnCancel');
    const formTitle = document.getElementById('formTitle');
    const idField = document.getElementById('materiaId');

    let editingId = null;

    function cargarMaterias() {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-state">Cargando...</td></tr>';
        api.get('/materias')
            .then(materias => {
                if (materias.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="6" class="empty-state">No hay materias registradas</td></tr>';
                    return;
                }
                tbody.innerHTML = materias.map(m => `
                    <tr>
                        <td>${m.id}</td>
                        <td><strong>${escapeHtml(m.codigo)}</strong></td>
                        <td>${escapeHtml(m.nombre)}</td>
                        <td>${m.creditos}</td>
                        <td>${m.cupoMaximo}</td>
                        <td>
                            <div class="actions">
                                <button class="btn btn-sm" onclick="editarMateria(${m.id})">Editar</button>
                                <button class="btn btn-sm btn-danger" onclick="eliminarMateria(${m.id}, '${escapeHtml(m.nombre)}')">Eliminar</button>
                            </div>
                        </td>
                    </tr>
                `).join('');
            })
            .catch(err => {
                tbody.innerHTML = '<tr><td colspan="6" class="empty-state">Error: ' + escapeHtml(err.message) + '</td></tr>';
            });
    }

    form.addEventListener('submit', function (e) {
        e.preventDefault();
        limpiarErrores();
        ocultarAlerta();

        const data = {
            codigo: form.codigo.value.trim().toUpperCase(),
            nombre: form.nombre.value.trim(),
            creditos: parseInt(form.creditos.value),
            cupoMaximo: parseInt(form.cupoMaximo.value)
        };

        const promise = editingId
            ? api.put('/materias/' + editingId, data)
            : api.post('/materias', data);

        promise
            .then(() => {
                showToast(editingId ? 'Materia actualizada' : 'Materia creada', 'success');
                resetForm();
                cargarMaterias();
            })
            .catch(err => {
                mostrarAlerta(err.message, 'error');
            });
    });

    btnCancel.addEventListener('click', resetForm);

    window.editarMateria = function (id) {
        api.get('/materias/' + id)
            .then(m => {
                editingId = id;
                idField.value = id;
                form.codigo.value = m.codigo;
                form.nombre.value = m.nombre;
                form.creditos.value = m.creditos;
                form.cupoMaximo.value = m.cupoMaximo;
                formTitle.textContent = 'Editar Materia';
                btnSubmit.textContent = 'Actualizar';
                btnCancel.style.display = 'inline-block';
                form.scrollIntoView({ behavior: 'smooth' });
            })
            .catch(err => showToast(err.message, 'error'));
    };

    window.eliminarMateria = function (id, nombre) {
        if (!confirm('Eliminar "' + nombre + '"? Esta accion no se puede deshacer.')) return;
        api.del('/materias/' + id)
            .then(() => {
                showToast('Materia eliminada', 'success');
                if (editingId === id) resetForm();
                cargarMaterias();
            })
            .catch(err => showToast(err.message, 'error'));
    };

    function resetForm() {
        form.reset();
        idField.value = '';
        editingId = null;
        formTitle.textContent = 'Nueva Materia';
        btnSubmit.textContent = 'Guardar';
        btnCancel.style.display = 'none';
        limpiarErrores();
        ocultarAlerta();
    }

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

    document.getElementById('year').textContent = new Date().getFullYear();
    cargarMaterias();
})();
