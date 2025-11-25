// Component: Servicios
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-services',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="services-container">
      <section class="services-hero">
        <h1>🔬 Nuestros Servicios</h1>
        <p>Amplia gama de exámenes clínicos y de laboratorio</p>
      </section>
      <section class="services-content">
        <div class="services-grid">
        <div class="service-category">
          <h2>🩸 Hematología</h2>
          <ul>
            <li>Hemograma completo</li>
            <li>Velocidad de sedimentación</li>
            <li>Grupo sanguíneo y factor RH</li>
            <li>Tiempo de protrombina</li>
          </ul>
        </div>
        <div class="service-category">
          <h2>🧬 Bioquímica Clínica</h2>
          <ul>
            <li>Perfil lipídico completo</li>
            <li>Glicemia en ayunas</li>
            <li>Pruebas de función hepática</li>
            <li>Perfil renal</li>
          </ul>
        </div>
        <div class="service-category">
          <h2>💉 Inmunología</h2>
          <ul>
            <li>Perfil tiroideo</li>
            <li>Marcadores tumorales</li>
            <li>Pruebas de embarazo</li>
            <li>Alergias alimentarias</li>
          </ul>
        </div>
        <div class="service-category">
          <h2>💧 Urianálisis</h2>
          <ul>
            <li>Orina completa</li>
            <li>Urocultivo</li>
            <li>Sedimento urinario</li>
          </ul>
        </div>
        </div>
      </section>
      <section class="cta">
        <h2>¿Necesitas más información?</h2>
        <a routerLink="/contacto" class="btn btn-primary">Contáctanos</a>
      </section>
    </div>
  `,
  styles: [`
    .services-container { width: 100%; }
    .services-hero { background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%); color: white;
      padding: 3rem 2rem; text-align: center; }
    .services-hero h1 { font-size: 2.5rem; margin-bottom: 1rem; color: white; }
    .services-hero p { color: white; }
    .services-content { background: #f9fafb; padding: 4rem 2rem; width: 100%; }
    .services-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 2rem; max-width: 1200px; margin: 0 auto; }
    .service-category { background: white; padding: 2rem; border-radius: 12px;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
    .service-category h2 { color: #0369a1; margin-bottom: 1.5rem; }
    ul { list-style: none; padding: 0; }
    li { padding: 0.5rem 0; color: #555; border-bottom: 1px solid #f0f0f0; }
    li:last-child { border-bottom: none; }
    .cta { background: #f9fafb; padding: 3rem 2rem; text-align: center; }
    .cta h2 { color: #333; margin-bottom: 1.5rem; }
    .btn { padding: 0.875rem 2rem; background: #0369a1; color: white; text-decoration: none;
      border-radius: 8px; display: inline-block; font-weight: 600; transition: all 0.3s;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
    .btn:hover { background: #075985; box-shadow: 0 10px 20px rgba(0,0,0,0.2); transform: translateY(-2px); }
    @media (max-width: 767px) {
      .services-hero h1 { font-size: 2rem; }
      .services-content { grid-template-columns: 1fr; }
    }
  `]
})
export class ServicesComponent {}
