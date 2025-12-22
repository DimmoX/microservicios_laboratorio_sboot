// Component: Nosotros
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="about-container">
      <section class="about-hero">
        <h1>🏥 Sobre Nosotros</h1>
        <p>Más de 30 años brindando servicios de diagnóstico de excelencia</p>
      </section>
      <section class="about-content">
        <div class="story">
          <h2>📖 Nuestra Historia</h2>
          <p>Fundados en 1995, Laboratorios Clínicos Andino nació con la misión de brindar servicios de diagnóstico
          de alta calidad a la comunidad. Con el paso de los años, nos hemos consolidado como líderes en el sector,
          expandiéndonos a múltiples ciudades de Chile.</p>
        </div>
        <div class="mission">
          <h2>🎯 Misión y Visión</h2>
          <div class="mv-cards">
            <div class="mv-card">
              <h3>Misión</h3>
              <p>Proporcionar resultados de diagnóstico precisos y oportunos, contribuyendo al bienestar de nuestros pacientes.</p>
            </div>
            <div class="mv-card">
              <h3>Visión</h3>
              <p>Ser el laboratorio clínico líder en Chile, reconocido por nuestra excelencia técnica y atención humana.</p>
            </div>
          </div>
        </div>
        <div class="values">
          <h2>💎 Nuestros Valores</h2>
          <ul>
            <li>✓ Excelencia profesional</li>
            <li>✓ Compromiso con la calidad</li>
            <li>✓ Ética y transparencia</li>
            <li>✓ Innovación tecnológica</li>
            <li>✓ Atención centrada en el paciente</li>
          </ul>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .about-container { width: 100%; }
    .about-hero { background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%); color: white;
      padding: 3rem 2rem; text-align: center; }
    .about-hero h1 { font-size: 2.5rem; margin-bottom: 1rem; color: white; }
    .about-hero p { color: white; font-size: 1.2rem; }
    .about-content { background: white; padding: 4rem 2rem; width: 100%; }
    .about-content > * { max-width: 1000px; margin: 0 auto; }
    .story, .mission, .values { margin-bottom: 3rem; }
    h2 { color: #333; font-size: 1.8rem; margin-bottom: 1.5rem; }
    p { line-height: 1.8; color: #555; font-size: 1rem; }
    .mv-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 2rem; }
    .mv-card { background: #f9fafb; padding: 2rem; border-radius: 12px; border-left: 4px solid #0369a1; transition: transform 0.3s; }
    .mv-card:hover { transform: translateX(5px); }
    .mv-card h3 { color: #0369a1; margin-bottom: 1rem; }
    ul { list-style: none; padding: 0; }
    li { padding: 0.75rem 0; font-size: 1.1rem; color: #555; }
    
    /* Mobile */
    @media (max-width: 767px) {
      .about-hero { padding: 2rem 1rem; }
      .about-hero h1 { font-size: 1.75rem; }
      .about-hero p { font-size: 1rem; }
      .about-content { padding: 2rem 1rem; }
      h2 { font-size: 1.5rem; }
      p { font-size: 0.95rem; }
      .mv-cards { grid-template-columns: 1fr; gap: 1.5rem; }
      .mv-card { padding: 1.5rem; }
      li { font-size: 1rem; padding: 0.5rem 0; }
    }
    
    /* Tablet */
    @media (min-width: 768px) and (max-width: 1023px) {
      .about-hero { padding: 2.5rem 1.5rem; }
      .about-hero h1 { font-size: 2rem; }
      .about-content { padding: 3rem 1.5rem; }
      h2 { font-size: 1.6rem; }
    }
  `]
})
export class AboutComponent {}
