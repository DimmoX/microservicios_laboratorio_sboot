package com.gestion_users.ms_gestion_users.service.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gestion_users.ms_gestion_users.dto.UsuarioResponse;
import com.gestion_users.ms_gestion_users.dto.UsuarioUpdateRequest;
import com.gestion_users.ms_gestion_users.model.EmpleadoModel;
import com.gestion_users.ms_gestion_users.model.PacienteModel;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.repository.ContactoRepository;
import com.gestion_users.ms_gestion_users.repository.DireccionRepository;
import com.gestion_users.ms_gestion_users.repository.EmpleadoRepository;
import com.gestion_users.ms_gestion_users.repository.PacienteRepository;

@Service
public class UsuarioProfileService {

    private final PacienteRepository pacienteRepo;
    private final EmpleadoRepository empleadoRepo;
    private final ContactoRepository contactoRepo;
    private final DireccionRepository direccionRepo;

    public UsuarioProfileService(
        PacienteRepository pacienteRepo,
        EmpleadoRepository empleadoRepo,
        ContactoRepository contactoRepo,
        DireccionRepository direccionRepo
    ) {
        this.pacienteRepo = pacienteRepo;
        this.empleadoRepo = empleadoRepo;
        this.contactoRepo = contactoRepo;
        this.direccionRepo = direccionRepo;
    }

    public UsuarioResponse buildProfile(UsuarioModel user) {
        UsuarioResponse out = new UsuarioResponse();
        out.setId(user.getId());
        out.setUsername(user.getUsername());
        out.setRol(normalizeRole(user.getRole()));
        out.setActivo("ACTIVO".equalsIgnoreCase(user.getEstado()));
        out.setPacienteId(user.getPacienteId());
        out.setEmpleadoId(user.getEmpleadoId());

        // Resolver info extendida desde paciente/empleado
        if (user.getPacienteId() != null) {
            Optional<PacienteModel> pacienteOpt = pacienteRepo.findById(user.getPacienteId());
            if (pacienteOpt.isPresent()) {
                PacienteModel paciente = pacienteOpt.get();
                out.setNombre(buildNombre(paciente.getPnombre(), paciente.getSnombre(), paciente.getPapellido(), paciente.getSapellido()));
                out.setRut(paciente.getRut());
                out.setContactoId(paciente.getContactoId());
                out.setDirId(paciente.getDirId());
                hydrateContactoDireccion(out, paciente.getContactoId(), paciente.getDirId());
            }
        } else if (user.getEmpleadoId() != null) {
            Optional<EmpleadoModel> empleadoOpt = empleadoRepo.findById(user.getEmpleadoId());
            if (empleadoOpt.isPresent()) {
                EmpleadoModel empleado = empleadoOpt.get();
                out.setNombre(buildNombre(empleado.getPnombre(), empleado.getSnombre(), empleado.getPapellido(), empleado.getSapellido()));
                out.setRut(empleado.getRut());
                out.setCargo(empleado.getCargo());
                out.setContactoId(empleado.getContactoId());
                out.setDirId(empleado.getDirId());
                hydrateContactoDireccion(out, empleado.getContactoId(), empleado.getDirId());
            }
        }

        // Fallback de nombre si no hay datos relacionados
        if (out.getNombre() == null || out.getNombre().isBlank()) {
            out.setNombre(fallbackNombreFromUsername(user.getUsername()));
        }

        return out;
    }

    public void applyUpdates(UsuarioModel user, UsuarioUpdateRequest updates) {
        if (updates == null) return;

        if (updates.getActivo() != null) {
            user.setEstado(updates.getActivo() ? "ACTIVO" : "INACTIVO");
        }

        if (updates.getRol() != null && !updates.getRol().isBlank()) {
            user.setRole(normalizeRole(updates.getRol()));
        }

        // Actualizar paciente/empleado (nombre/telefono/direccion)
        if (user.getPacienteId() != null) {
            pacienteRepo.findById(user.getPacienteId()).ifPresent(paciente -> {
                if (updates.getNombre() != null && !updates.getNombre().isBlank()) {
                    applyNombreToPaciente(paciente, updates.getNombre());
                }
                if (updates.getTelefono() != null) {
                    updateContactoTelefono(paciente.getContactoId(), updates.getTelefono());
                }
                if (updates.getDireccion() != null) {
                    updateDireccionCalle(paciente.getDirId(), updates.getDireccion());
                }
                pacienteRepo.save(paciente);
            });
        } else if (user.getEmpleadoId() != null) {
            empleadoRepo.findById(user.getEmpleadoId()).ifPresent(empleado -> {
                if (updates.getNombre() != null && !updates.getNombre().isBlank()) {
                    applyNombreToEmpleado(empleado, updates.getNombre());
                }
                if (updates.getTelefono() != null) {
                    updateContactoTelefono(empleado.getContactoId(), updates.getTelefono());
                }
                if (updates.getDireccion() != null) {
                    updateDireccionCalle(empleado.getDirId(), updates.getDireccion());
                }
                empleadoRepo.save(empleado);
            });
        }
    }

    private void hydrateContactoDireccion(UsuarioResponse out, Long contactoId, Long dirId) {
        if (contactoId != null) {
            contactoRepo.findById(contactoId).ifPresent(contacto -> {
                out.setTelefono(contacto.getFono1());
            });
        }
        if (dirId != null) {
            direccionRepo.findById(dirId).ifPresent(dir -> {
                out.setDireccion(dir.getCalle());
            });
        }
    }

    private void updateContactoTelefono(Long contactoId, String telefono) {
        if (contactoId == null) return;
        contactoRepo.findById(contactoId).ifPresent(contacto -> {
            contacto.setFono1(telefono);
            contactoRepo.save(contacto);
        });
    }

    private void updateDireccionCalle(Long dirId, String direccion) {
        if (dirId == null) return;
        direccionRepo.findById(dirId).ifPresent(dir -> {
            dir.setCalle(direccion);
            direccionRepo.save(dir);
        });
    }

    private static String buildNombre(String pnombre, String snombre, String papellido, String sapellido) {
        StringBuilder sb = new StringBuilder();
        appendIfPresent(sb, pnombre);
        appendIfPresent(sb, snombre);
        appendIfPresent(sb, papellido);
        appendIfPresent(sb, sapellido);
        String full = sb.toString().trim();
        return full.isEmpty() ? null : full;
    }

    private static void appendIfPresent(StringBuilder sb, String part) {
        if (part == null) return;
        String p = part.trim();
        if (p.isEmpty()) return;
        if (!sb.isEmpty()) sb.append(' ');
        sb.append(p);
    }

    private static String fallbackNombreFromUsername(String username) {
        if (username == null) return "Usuario";
        String u = username.trim();
        if (u.isEmpty()) return "Usuario";
        int at = u.indexOf('@');
        if (at > 0) u = u.substring(0, at);
        return u;
    }

    private static String normalizeRole(String role) {
        if (role == null) return null;
        String r = role.trim();
        if (r.startsWith("ROLE_")) r = r.substring("ROLE_".length());
        return r;
    }

    private static void applyNombreToPaciente(PacienteModel paciente, String nombreCompleto) {
        String[] parts = nombreCompleto.trim().split("\\s+");
        if (parts.length == 0) return;
        paciente.setPnombre(parts[0]);
        if (parts.length >= 2) paciente.setPapellido(parts[parts.length - 1]);
        if (parts.length >= 3) paciente.setSnombre(parts[1]);
        if (parts.length >= 4) paciente.setSapellido(parts[2]);
    }

    private static void applyNombreToEmpleado(EmpleadoModel empleado, String nombreCompleto) {
        String[] parts = nombreCompleto.trim().split("\\s+");
        if (parts.length == 0) return;
        empleado.setPnombre(parts[0]);
        if (parts.length >= 2) empleado.setPapellido(parts[parts.length - 1]);
        if (parts.length >= 3) empleado.setSnombre(parts[1]);
        if (parts.length >= 4) empleado.setSapellido(parts[2]);
    }
}
