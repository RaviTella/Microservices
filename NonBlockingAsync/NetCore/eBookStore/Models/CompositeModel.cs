using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace eBookStore.Models
{
    public class CompositeModel
    {
        public IEnumerable<Item> Recommendations { get; set; }
        public IEnumerable<Item> ViewedItems { get; set; }
        public IEnumerable<Customer> Customers { get; set; }
        public IEnumerable<Item> CartItems { get; set; }
        public string RecommendationsResposne {get;set;}


    }
}
