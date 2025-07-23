import { Button } from "@/components/ui/button";
import { Lock, Shield } from "lucide-react";

const Hero = () => {
  return (
    <section className="relative min-h-screen flex items-center justify-center overflow-hidden">
      <div className="absolute inset-0 bg-gradient-hero opacity-90" />

      <div className="absolute top-20 left-10 w-32 h-32 bg-primary-glow/20 rounded-full blur-3xl" />
      <div className="absolute bottom-20 right-10 w-40 h-40 bg-accent/20 rounded-full blur-3xl" />

      <div className="relative z-10 container mx-auto px-6 text-center text-white">
        <div className="max-w-4xl mx-auto animate-fade-in">
          <h1 className="text-5xl md:text-7xl font-bold mb-6 leading-tight">
            Welcome to{" "}
            <span className="bg-gradient-to-r from-white to-accent bg-clip-text text-transparent">
              Spring Bank
            </span>
          </h1>

          <p className="text-xl md:text-2xl mb-8 text-white/90 leading-relaxed">
            Your complete digital banking solution. Manage accounts, track
            transactions, and apply for loans with confidence and ease.
          </p>

          <div className="flex flex-col sm:flex-row gap-4 justify-center mb-12">
            <Button
              variant="default"
              className="text-2xl px-12 py-6"
              onClick={() => (window.location.href = "/login")}
            >
              Login
              <Lock className="ml-2 h-6 w-6" />
            </Button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-1 gap-6 max-w-2xl mx-auto">
            <div className="flex items-center justify-center gap-3 text-white/80">
              <Shield className="h-6 w-6 text-accent" />
              <span>Bank-Grade Security</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Hero;
