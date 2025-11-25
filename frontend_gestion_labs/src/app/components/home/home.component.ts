// Component: Home - Página principal informativa
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="home-container">
      <!-- Hero Section -->
      <section class="hero">
        <div class="hero-content">
          <h1>🏥 Laboratorios Clínicos Andino</h1>
          <p class="subtitle">Excelencia en diagnóstico médico desde 1995</p>
          <p class="description">
            Contamos con tecnología de última generación y un equipo de profesionales 
            altamente capacitados para brindarle resultados precisos y confiables.
          </p>
          <div class="hero-buttons">
            <a routerLink="/servicios" class="btn btn-primary">Ver Servicios</a>
            <a *ngIf="!isAuthenticated" routerLink="/register" class="btn btn-secondary">Registrarse</a>
            <a *ngIf="!isAuthenticated" routerLink="/login" class="btn btn-outline">Iniciar Sesión</a>
          </div>
        </div>
      </section>

      <!-- Servicios Destacados -->
      <section class="features">
        <h2>¿Por qué elegirnos?</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="icon">🔬</div>
            <h3>Tecnología Avanzada</h3>
            <p>Equipos de última generación para resultados precisos</p>
          </div>
          <div class="feature-card">
            <div class="icon">⚡</div>
            <h3>Resultados Rápidos</h3>
            <p>Entrega de resultados en tiempo récord</p>
          </div>
          <div class="feature-card">
            <div class="icon">👨‍⚕️</div>
            <h3>Profesionales Calificados</h3>
            <p>Equipo médico con amplia experiencia</p>
          </div>
          <div class="feature-card">
            <div class="icon">🏆</div>
            <h3>Certificación ISO</h3>
            <p>Calidad garantizada con certificación internacional</p>
          </div>
        </div>
      </section>

      <!-- Exámenes Populares -->
      <section class="exams">
        <h2>Exámenes Más Solicitados</h2>
        <div class="exams-grid">
          <div class="exam-card">
            <h4>🩸 Hemograma Completo</h4>
            <p>Análisis completo de células sanguíneas</p>
            <span class="price">Desde $15.000</span>
          </div>
          <div class="exam-card">
            <h4>💉 Perfil Lipídico</h4>
            <p>Colesterol, HDL, LDL y triglicéridos</p>
            <span class="price">Desde $18.000</span>
          </div>
          <div class="exam-card">
            <h4>🧪 Perfil Tiroideo</h4>
            <p>TSH, T3 y T4</p>
            <span class="price">Desde $25.000</span>
          </div>
          <div class="exam-card">
            <h4>💧 Orina Completa</h4>
            <p>Análisis físico, químico y microscópico</p>
            <span class="price">Desde $10.000</span>
          </div>
        </div>
      </section>

      <!-- CTA -->
      <section class="cta">
        <h2>¿Listo para agendar tu examen?</h2>
        <p>Regístrate ahora y accede a todos nuestros servicios</p>
        <a *ngIf="!isAuthenticated" routerLink="/register" class="btn btn-primary btn-large">Comenzar Ahora</a>
        <a *ngIf="isAuthenticated" routerLink="/laboratorios" class="btn btn-primary btn-large">Ver Laboratorios</a>
      </section>
    </div>
  `,
  styles: [`
    .home-container { width: 100%; }
    .hero { background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%); color: white;
      padding: 4rem 2rem; text-align: center; }
    .hero-content { max-width: 800px; margin: 0 auto; }
    .hero h1 { font-size: 3rem; margin-bottom: 1rem; color: white; }
    .subtitle { font-size: 1.5rem; margin-bottom: 1rem; opacity: 0.95; color: white; }
    .description { font-size: 1.1rem; margin-bottom: 2rem; line-height: 1.6; color: white; }
    .hero-buttons { display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap; }
    .btn { padding: 0.875rem 2rem; border-radius: 8px; text-decoration: none; font-weight: 600;
      transition: all 0.3s; display: inline-block; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
    .btn-primary { background: white; color: #0369a1; }
    .btn-secondary { background: #fbbf24; color: #000; }
    .btn-outline { background: transparent; color: white; border: 2px solid white; }
    .btn:hover { transform: translateY(-2px); box-shadow: 0 10px 20px rgba(0,0,0,0.2); }
    .features, .exams, .cta { padding: 4rem 2rem; }
    .features { background-color: #f9fafb; }
    .features h2 { text-align: center; font-size: 2rem; margin-bottom: 3rem; color: #333; }
    .exams { background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%); }
    .exams h2 { text-align: center; font-size: 2rem; margin-bottom: 3rem; color: white; }
    .cta h2 { text-align: center; font-size: 2rem; margin-bottom: 3rem; color: white; }
    .features-grid, .exams-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 2rem; max-width: 1200px; margin: 0 auto; }
    .feature-card, .exam-card { background: white; padding: 2rem; border-radius: 12px;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1); text-align: center; transition: transform 0.3s; }
    .feature-card:hover, .exam-card:hover { transform: translateY(-5px); }
    .icon { font-size: 3rem; margin-bottom: 1rem; }
    .feature-card h3, .exam-card h4 { color: #333; margin-bottom: 0.5rem; }
    .feature-card p, .exam-card p { color: #666; line-height: 1.6; }
    .price { display: block; margin-top: 1rem; font-size: 1.2rem; font-weight: 600; color: #0369a1; }
    .cta { background: white; text-align: center; }
    .cta h2 { color: #333; }
    .cta p { font-size: 1.2rem; margin-bottom: 2rem; color: #555; }
    .btn-large { font-size: 1.2rem; padding: 1rem 2.5rem; }
    /* Mobile */
    @media (max-width: 767px) {
      .hero h1 { font-size: 2rem; }
      .subtitle { font-size: 1.2rem; }
      .hero-buttons { flex-direction: column; }
      .features-grid, .exams-grid { grid-template-columns: 1fr; }
    }
    /* Tablet */
    @media (min-width: 768px) and (max-width: 1024px) {
      .features-grid, .exams-grid { grid-template-columns: repeat(2, 1fr); }
    }
  `]
})
export class HomeComponent {
  constructor(public authService: AuthService) {}
  get isAuthenticated() { return this.authService.isAuthenticated(); }
}
