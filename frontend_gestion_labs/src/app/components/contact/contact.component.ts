// Component: Contacto
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="contact-container">
      <section class="contact-hero">
        <h1>📞 Contáctanos</h1>
        <p>Estamos aquí para atenderte</p>
      </section>
      <section class="contact-content">
        <div class="contact-grid">
        <div class="contact-info">
          <h2>📍 Información de Contacto</h2>
          <div class="info-item">
            <strong>🏢 Casa Matriz:</strong>
            <p>Av. Libertador Bernardo O'Higgins 1234<br>Santiago Centro, Santiago</p>
          </div>
          <div class="info-item">
            <strong>📱 Teléfonos:</strong>
            <p>+56 2 2345 6789<br>+56 9 8765 4321</p>
          </div>
          <div class="info-item">
            <strong>✉️ Email:</strong>
            <p>contacto&#64;laboratorioandino.cl</p>
          </div>
          <div class="info-item">
            <strong>🕐 Horario de Atención:</strong>
            <p>Lunes a Viernes: 8:00 - 20:00<br>Sábados: 9:00 - 14:00</p>
          </div>
        </div>
        <div class="contact-form-container">
          <h2>💬 Envíanos un Mensaje</h2>
          <form (ngSubmit)="onSubmit()" #contactForm="ngForm" class="contact-form">
            <input type="text" [(ngModel)]="formData.nombre" name="nombre" placeholder="Nombre" required class="form-control">
            <input type="email" [(ngModel)]="formData.email" name="email" placeholder="Email" required class="form-control">
            <input type="tel" [(ngModel)]="formData.telefono" name="telefono" placeholder="Teléfono" class="form-control">
            <textarea [(ngModel)]="formData.mensaje" name="mensaje" rows="5" placeholder="Mensaje" required class="form-control"></textarea>
            <div class="alert alert-success" *ngIf="submitted">✅ Mensaje enviado correctamente. Te contactaremos pronto.</div>
            <button type="submit" class="btn btn-primary">Enviar Mensaje</button>
          </form>
        </div>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .contact-container { width: 100%; }
    .contact-hero { background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%); color: white;
      padding: 3rem 2rem; text-align: center; }
    .contact-hero h1 { font-size: 2.5rem; margin-bottom: 1rem; color: white; }
    .contact-hero p { color: white; }
    .contact-content { background: #f9fafb; padding: 4rem 2rem; width: 100%; }
    .contact-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 3rem; max-width: 1200px; margin: 0 auto; }
    .contact-info, .contact-form-container { background: white; padding: 2rem; border-radius: 12px;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
    h2 { color: #333; margin-bottom: 1.5rem; }
    .info-item { margin-bottom: 1.5rem; }
    .info-item strong { color: #0369a1; display: block; margin-bottom: 0.5rem; }
    .info-item p { color: #555; line-height: 1.6; margin: 0; }
    .contact-form { display: flex; flex-direction: column; gap: 1rem; }
    .form-control { padding: 0.75rem; border: 2px solid #e0e0e0; border-radius: 8px;
      font-size: 1rem; font-family: inherit; }
    .form-control:focus { outline: none; border-color: #0369a1; box-shadow: 0 0 0 3px rgba(3,105,161,0.1); }
    .btn { padding: 0.875rem; background: #0369a1; color: white; border: none; border-radius: 8px;
      font-weight: 600; cursor: pointer; transition: all 0.3s; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
    .btn:hover { background: #075985; box-shadow: 0 10px 20px rgba(0,0,0,0.2); transform: translateY(-2px); }
    .alert-success { background: #d1fae5; color: #065f46; padding: 1rem; border-radius: 8px; }
    @media (max-width: 767px) {
      .contact-hero h1 { font-size: 2rem; }
      .contact-content { grid-template-columns: 1fr; }
    }
  `]
})
export class ContactComponent {
  formData = { nombre: '', email: '', telefono: '', mensaje: '' };
  submitted = false;

  onSubmit() {
    console.log('Formulario enviado:', this.formData);
    this.submitted = true;
    setTimeout(() => {
      this.formData = { nombre: '', email: '', telefono: '', mensaje: '' };
      this.submitted = false;
    }, 3000);
  }
}
