using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using eBookStore.Models;
using System.Net.Http;
using Newtonsoft.Json;

namespace eBookStore.Controllers
{
    public class HomeController : Controller
    {
        private static HttpClient Client = new HttpClient();
        public async Task<IActionResult>  Index()
        {
            var recommendationsTask = Task.Run(()=>Client.GetStringAsync("http://52.191.234.154:9000/recommendations/customer/1001"));
            var viewedItemsTask = Task.Run(() => Client.GetStringAsync("http://52.224.136.196:9000/viewedItems/customer/1001"));
            var cartTask = Task.Run(() => Client.GetStringAsync("http://52.191.234.203:9000/cart/customer/1001"));
            var customerTask = Task.Run(() => Client.GetStringAsync("http://52.190.26.105:9000/customer/1001"));            

            await Task.WhenAll(recommendationsTask, viewedItemsTask,cartTask,customerTask);
            CompositeModel model = new CompositeModel
            {
                Recommendations = JsonConvert.DeserializeObject<IEnumerable<Item>>(recommendationsTask.Result),
                ViewedItems = JsonConvert.DeserializeObject<IEnumerable<Item>>(viewedItemsTask.Result),
                CartItems = JsonConvert.DeserializeObject<IEnumerable<Item>>(cartTask.Result),
                Customers = JsonConvert.DeserializeObject<IEnumerable<Customer>>(customerTask.Result)
            };
            return View(model);
        }

        public IActionResult About()
        {
            ViewData["Message"] = "Your application description page.";

            return View();
        }

        public IActionResult Contact()
        {
            ViewData["Message"] = "Your contact page.";

            return View();
        }

        public IActionResult Privacy()
        {
            return View();
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}
