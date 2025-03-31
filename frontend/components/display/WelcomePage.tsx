import { Button } from '@/components/ui/button';
import Link from 'next/link';

export default function WelcomePage() {
  return (
    <div className="flex flex-col justify-center items-center min-h-[calc(100vh-160px)] md:grid md:grid-cols-2">
      <div className="flex flex-col justify-center items-center py-8 md:py-0">
        <h1 className="scroll-m-20 text-2xl font-bold tracking-tight lg:text-4xl text-center px-4">
          Welcome to Agile Auctions!
        </h1>
      </div>
      <div className="flex flex-col md:flex-row justify-center items-center md:border-l gap-4 py-8 md:py-0">
        <Link href="/login">
          <Button className="text-lg px-7 py-5 cursor-pointer w-full md:w-auto">Login</Button>
        </Link>
        <Link href="/register">
          <Button className="text-lg px-7 py-5 cursor-pointer w-full md:w-auto">Register</Button>
        </Link>
      </div>
    </div>
  );
}
