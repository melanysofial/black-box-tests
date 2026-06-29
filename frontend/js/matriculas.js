/**
 * Gestion de Matriculas — Matricular y Calificar.
 * Reglas de negocio para pruebas de:
 *   - Tablas de decision (5 condiciones para matricular)
 *   - Transicion de estados (MATRICULADO -> APROBADO/REPROBADO)
 *   - Casos de uso (flujo principal y alternativos)
 */
(function () {
    'use strict';

    const matriculaForm = document.getElementById('matriculaForm');
    const notaForm = document.getElementById('notaForm');
    const alertBox = document.getElementById('alertBox');
    const tbody = document.getElementById('matriculasTableBody');
    const selectEstudiante = document.getElementById('estudianteId');
    const selectMateria = document.getElementById('materiaId');
    const filtroEstudiante = document.getElementById('filtroEstudiante');

    // --- Cargar selects ---
    function cargarSelects() {
        api.get('/estudiantes')
            .then(estudiantes => {
                const html = estudiantes.map(e =>
                    `<option value="${e.id}">${e.id} — ${e.nombre} ${e.apellido} (${e.estado})</option>`
                ).join('');
                selectEstudiante.innerHTML = '<option value="">— Seleccione —</option>' + html;
                filtroEstudiante.innerHTML = '<option value="">Filtrar por estudiante...</option>' + html;
            })
            .catch(err => showToast('Error al cargar estudiantes: ' + err.message, 'error'));

        api.get('/materias')
            .then(materias => {
                selectMateria.innerHTML = '<option value="">— Seleccione —</option>' +
                    materias.map(m => `<option value="${m.id}">${m.id} — ${m.codigo} ${m.nombre} (Cupo: ${m.cupoMaximo})</option>`).join('');
            })
            .catch(err => showToast('Error al cargar materias: ' + err.message, 'error'));
    }

    // --- Cargar tabla de matriculas ---
    function cargarMatriculas() {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-state">Cargando...</td></tr>';
        api.get('/matriculas')
            .then(matriculas => renderMatriculas(matriculas))
            .catch(err => {
                tbody.innerHTML = '<tr><td colspan="6" class="empty-state">Error: ' + escapeHtml(err.message) + '</td></tr>';
            });
    }

    function renderMatriculas(matriculas) {
        if (matriculas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="empty-state">No hay matriculas registradas</td></tr>';
            return;
        }
        tbody.innerHTML = matriculas.map(m => `
            <tr>
                <td><strong>${m.id}</strong></td>
                <td>${escapeHtml(m.estudiante.nombre)} ${escapeHtml(m.estudiante.apellido)}</td>
                <td>${escapeHtml(m.materia.codigo)} — ${escapeHtml(m.materia.nombre)}</td>
                <td>${m.nota != null ? m.nota : '—'}</td>
                <td><span class="badge badge-${m.estado.toLowerCase()}">${m.estado}</span></td>
                <td>
                    <button class="btn btn-sm btn-danger" onclick="eliminarMatricula(${m.id})">Eliminar</button>
                </td>
            </tr>
        `).join('');
    }

    window.filtrarPorEstudiante = function () {
        const id = filtroEstudiante.value;
        if (!id) { cargarMatriculas(); return; }
        tbody.innerHTML = '<tr><td colspan="6" class="empty-state">Cargando...</td></tr>';
        api.get('/matriculas/estudiante/' + id)
            .then(matriculas => renderMatriculas(matriculas))
            .catch(err => {
                tbody.innerHTML = '<tr><td colspan="6" class="empty-state">Error: ' + escapeHtml(err.message) + '</td></tr>';
            });
    };

    // --- Matricular ---
    matriculaForm.addEventListener('submit', function (e) {
        e.preventDefault();
        ocultarAlerta();

        const data = {
            estudianteId: parseInt(selectEstudiante.value),
            materiaId: parseInt(selectMateria.value)
        };

        api.post('/matriculas', data)
            .then(() => {
                showToast('Estudiante matriculado exitosamente', 'success');
                matriculaForm.reset();
                cargarMatriculas();
            })
            .catch(err => {
                mostrarAlerta(err.message, 'error');
            });
    });

    // --- Calificar ---
    notaForm.addEventListener('submit', function (e) {
        e.preventDefault();
        ocultarAlerta();

        const id = parseInt(notaForm.matriculaId.value);
        const notaValor = notaForm.nota.value;

        if (!notaValor || isNaN(parseFloat(notaValor))) {
            mostrarAlerta('La nota debe ser un numero valido', 'error');
            return;
        }

        api.put('/matriculas/' + id + '/calificar', { nota: notaValor })
            .then(m => {
                showToast('Nota registrada. Estado: ' + m.estado, 'success');
                notaForm.reset();
                cargarMatriculas();
            })
            .catch(err => {
                mostrarAlerta(err.message, 'error');
            });
    });

    // --- Eliminar matricula ---
    window.eliminarMatricula = function (id) {
        if (!confirm('Eliminar matricula #' + id + '?')) return;
        api.del('/matriculas/' + id)
            .then(() => {
                showToast('Matricula eliminada', 'success');
                cargarMatriculas();
            })
            .catch(err => showToast(err.message, 'error'));
    };

    // --- Helpers ---
    function mostrarAlerta(msg, type) {
        alertBox.textContent = msg;
        alertBox.className = 'alert alert-' + type + ' visible';
    }

    function ocultarAlerta() {
        alertBox.className = 'alert';
    }

    function escapeHtml(str) {
        if (!str) return '';
        return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }

    // --- Inicializar ---
    document.getElementById('year').textContent = new Date().getFullYear();
    cargarSelects();
    cargarMatriculas();
})();
