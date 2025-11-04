package com.gestion_users.ms_gestion_users.service.registro;

import com.gestion_users.ms_gestion_users.dto.*;
import com.gestion_users.ms_gestion_users.model.*;
import com.gestion_users.ms_gestion_users.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para el registro completo de pacientes y empleados.
 * Maneja la creación transaccional de:
 * 1. Contacto (incluye email)
 * 2. Dirección
 * 3. Paciente/Empleado
 * 4. Usuario (credenciales de login)
 */
@Service
public class RegistroServiceImpl implements RegistroService {

    private static final Logger logger = LoggerFactory.getLogger(RegistroServiceImpl.class);

    private final ContactoRepository contactoRepo;
    private final DireccionRepository direccionRepo;
    private final PacienteRepository pacienteRepo;
    private final EmpleadoRepository empleadoRepo;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    public RegistroServiceImpl(
            ContactoRepository contactoRepo,
            DireccionRepository direccionRepo,
            PacienteRepository pacienteRepo,
            EmpleadoRepository empleadoRepo,
            UsuarioRepository usuarioRepo,
            PasswordEncoder passwordEncoder) {
        this.contactoRepo = contactoRepo;
        this.direccionRepo = direccionRepo;
        this.pacienteRepo = pacienteRepo;
        this.empleadoRepo = empleadoRepo;
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public RegistroResponse registrarPaciente(RegistroPacienteRequest request) {
        logger.info("Iniciando registro de paciente con email: {}", request.getContacto().getEmail());

        // Validar que el email no esté registrado
        if (contactoRepo.findByEmail(request.getContacto().getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado: " + request.getContacto().getEmail());
        }

        if (usuarioRepo.findByUsername(request.getContacto().getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con este email: " + request.getContacto().getEmail());
        }

        try {
            // 1. Crear contacto
            ContactoModel contacto = new ContactoModel(
                request.getContacto().getFono1(),
                request.getContacto().getFono2(),
                request.getContacto().getEmail()
            );
            contacto = contactoRepo.save(contacto);
            logger.info("Contacto creado con ID: {}", contacto.getId());

            // 2. Crear dirección
            DireccionModel direccion = new DireccionModel(
                request.getDireccion().getCalle(),
                request.getDireccion().getNumero(),
                request.getDireccion().getCiudad(),
                request.getDireccion().getComuna(),
                request.getDireccion().getRegion()
            );
            direccion = direccionRepo.save(direccion);
            logger.info("Dirección creada con ID: {}", direccion.getId());

            // 3. Crear paciente
            PacienteModel paciente = new PacienteModel();
            paciente.setPnombre(request.getPnombre());
            paciente.setSnombre(request.getSnombre());
            paciente.setPapellido(request.getPapellido());
            paciente.setSapellido(request.getSapellido());
            paciente.setRut(request.getRut());
            paciente.setContactoId(contacto.getId());
            paciente.setDirId(direccion.getId());
            paciente = pacienteRepo.save(paciente);
            logger.info("Paciente creado con ID: {}", paciente.getId());

            // 4. Crear usuario para login
            UsuarioModel usuario = new UsuarioModel();
            usuario.setUsername(contacto.getEmail());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setRole("PATIENT");
            usuario.setPacienteId(paciente.getId());
            usuario.setEmpleadoId(null);
            usuario = usuarioRepo.save(usuario);
            logger.info("Usuario creado con ID: {} para paciente ID: {}", usuario.getId(), paciente.getId());

            return RegistroResponse.forPaciente(paciente.getId(), usuario.getId(), usuario.getUsername());

        } catch (Exception e) {
            logger.error("Error al registrar paciente: {}", e.getMessage(), e);
            throw new RuntimeException("Error al registrar paciente: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public RegistroResponse registrarEmpleado(RegistroEmpleadoRequest request) {
        logger.info("Iniciando registro de empleado con email: {}", request.getContacto().getEmail());

        // Validar que el email no esté registrado
        if (contactoRepo.findByEmail(request.getContacto().getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado: " + request.getContacto().getEmail());
        }

        if (usuarioRepo.findByUsername(request.getContacto().getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con este email: " + request.getContacto().getEmail());
        }

        try {
            // 1. Crear contacto
            ContactoModel contacto = new ContactoModel(
                request.getContacto().getFono1(),
                request.getContacto().getFono2(),
                request.getContacto().getEmail()
            );
            contacto = contactoRepo.save(contacto);
            logger.info("Contacto creado con ID: {}", contacto.getId());

            // 2. Crear dirección
            DireccionModel direccion = new DireccionModel(
                request.getDireccion().getCalle(),
                request.getDireccion().getNumero(),
                request.getDireccion().getCiudad(),
                request.getDireccion().getComuna(),
                request.getDireccion().getRegion()
            );
            direccion = direccionRepo.save(direccion);
            logger.info("Dirección creada con ID: {}", direccion.getId());

            // 3. Crear empleado
            EmpleadoModel empleado = new EmpleadoModel();
            empleado.setPnombre(request.getPnombre());
            empleado.setSnombre(request.getSnombre());
            empleado.setPapellido(request.getPapellido());
            empleado.setSapellido(request.getSapellido());
            empleado.setRut(request.getRut());
            empleado.setCargo(request.getCargo());
            empleado.setContactoId(contacto.getId());
            empleado.setDirId(direccion.getId());
            empleado = empleadoRepo.save(empleado);
            logger.info("Empleado creado con ID: {}", empleado.getId());

            // 4. Crear usuario para login
            UsuarioModel usuario = new UsuarioModel();
            usuario.setUsername(contacto.getEmail());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setRole("LAB_EMPLOYEE");
            usuario.setPacienteId(null);
            usuario.setEmpleadoId(empleado.getId());
            usuario = usuarioRepo.save(usuario);
            logger.info("Usuario creado con ID: {} para empleado ID: {}", usuario.getId(), empleado.getId());

            return RegistroResponse.forEmpleado(empleado.getId(), usuario.getId(), usuario.getUsername());

        } catch (Exception e) {
            logger.error("Error al registrar empleado: {}", e.getMessage(), e);
            throw new RuntimeException("Error al registrar empleado: " + e.getMessage());
        }
    }
}
