import { Component, OnInit, AfterViewInit, ElementRef, ViewChild, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingService } from '../../services/booking.service';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-stats',
  imports: [CommonModule],
  templateUrl: './stats.html'
})
export class Stats implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('monthChart') monthChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('typeChart') typeChartRef!: ElementRef<HTMLCanvasElement>;

  monthChart: Chart | null = null;
  typeChart: Chart | null = null;

  totalRequests = 0;
  totalTypes: { type: string; count: number }[] = [];
  topType = '';
  loading = true;
  error = false;

  monthData: any[] = [];
  typeData: any[] = [];
  private chartsReady = false;
  private dataReady = false;

  constructor(private bookingService: BookingService) {}

  ngOnInit() {
    let monthDone = false;
    let typeDone = false;

    this.bookingService.getStatsByMonth().subscribe({
      next: (data) => {
        this.monthData = data;
        monthDone = true;
        if (typeDone) this.onDataReady();
      },
      error: () => { this.error = true; this.loading = false; }
    });

    this.bookingService.getStatsByType().subscribe({
      next: (data) => {
        this.typeData = data;
        this.totalRequests = data.reduce((sum: number, d: any) => sum + d.count, 0);
        this.totalTypes = data;
        this.topType = data.length ? data.reduce((a: any, b: any) => a.count > b.count ? a : b).type : '-';
        typeDone = true;
        if (monthDone) this.onDataReady();
      },
      error: () => { this.error = true; this.loading = false; }
    });
  }

  ngAfterViewInit() {
    this.chartsReady = true;
    if (this.dataReady) this.buildCharts();
  }

  ngOnDestroy() {
    this.monthChart?.destroy();
    this.typeChart?.destroy();
  }

  private onDataReady() {
    this.loading = false;
    this.dataReady = true;
    if (this.chartsReady) this.buildCharts();
  }

  private buildCharts() {
    this.buildMonthChart();
    this.buildTypeChart();
  }

  private buildMonthChart() {
    const labels = this.monthData.map((d: any) => d.month);
    const counts = this.monthData.map((d: any) => d.count);

    this.monthChart = new Chart(this.monthChartRef.nativeElement, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Servicios solicitados',
          data: counts,
          backgroundColor: 'rgba(79, 70, 229, 0.8)',
          borderColor: 'rgba(79, 70, 229, 1)',
          borderWidth: 1,
          borderRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: (ctx) => ` ${ctx.parsed.y} servicio${ctx.parsed.y !== 1 ? 's' : ''}`
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: { stepSize: 1, precision: 0 },
            grid: { color: 'rgba(0,0,0,0.05)' }
          },
          x: {
            grid: { display: false }
          }
        }
      }
    });
  }

  private buildTypeChart() {
    const typeLabels: Record<string, string> = {
      HOGAR: 'Hogar',
      OFICINA: 'Oficina',
      PROFUNDA: 'Profunda',
      POST_OBRA: 'Post-Obra',
      VENTANAS: 'Ventanas'
    };
    const colors = ['#4F46E5', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'];

    const labels = this.typeData.map((d: any) => typeLabels[d.type] ?? d.type);
    const counts = this.typeData.map((d: any) => d.count);

    this.typeChart = new Chart(this.typeChartRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels,
        datasets: [{
          data: counts,
          backgroundColor: colors.slice(0, labels.length),
          borderWidth: 2,
          borderColor: '#fff'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: { padding: 16, font: { size: 13 } }
          },
          tooltip: {
            callbacks: {
              label: (ctx) => {
                const total = (ctx.dataset.data as number[]).reduce((a, b) => a + b, 0);
                const pct = total > 0 ? Math.round((ctx.parsed / total) * 100) : 0;
                return ` ${ctx.label}: ${ctx.parsed} (${pct}%)`;
              }
            }
          }
        }
      }
    });
  }
}
